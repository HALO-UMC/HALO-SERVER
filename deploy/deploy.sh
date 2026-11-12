#!/usr/bin/env bash
# 서버에서 실행하는 prod 배포 스크립트 (실패 시 직전 성공 버전으로 롤백)
# 사용법: 앱 디렉터리에서 IMAGE_TAG=<커밋 SHA> bash deploy/deploy.sh
# 전제: 호출 측에서 .env 생성, GHCR 로그인, .prev_release 백업(deploy-bootstrap)을 끝내둔 상태
set -euo pipefail

: "${IMAGE_TAG:?IMAGE_TAG가 필요합니다}"
export IMAGE_TAG

HEALTH_URL=http://127.0.0.1:8080/actuator/health

app_healthy() {
  curl -sf --max-time 5 "$HEALTH_URL" | grep -q '"status":"UP"'
}

# 최대 15회 x 4초 동안 app 헬스체크, $1은 대기 로그 문구
wait_app_healthy() {
  local label=$1
  for i in $(seq 1 15); do
    if app_healthy; then
      return 0
    fi
    echo "$label 대기 중... ($i/15)"
    sleep 4
  done
  return 1
}

# SHA 태그가 붙은 예전 app 이미지는 prune 대상이 아니라 계속 쌓이므로,
# 현재 버전과 직전 버전(롤백용)만 남기고 삭제. 정리 실패는 배포 결과에 영향 주지 않음
remove_old_app_images() {
  local repo
  repo=$(docker compose config --images app 2>/dev/null | head -n 1) || return 0
  repo=${repo%:*}
  [ -n "$repo" ] || return 0

  docker image ls "$repo" --format '{{.Repository}}:{{.Tag}}' \
    | grep -v -x -e "$repo:$IMAGE_TAG" -e "$repo:${PREV_SHA:-}" -e "$repo:<none>" \
    | xargs -r docker image rm >/dev/null 2>&1 || true
}

# 롤백 대비, 직전에 성공적으로 배포된 이미지의 SHA 태그 기록 (최초 배포면 빈 값)
PREV_SHA=$(cat .deployed_sha 2>/dev/null || true)

docker compose config >/dev/null

# elif 대신 개별 if: 앞 단계 실패 시 up을 건너뛰면 구버전이 헬스체크를 통과해 잘못된 SHA가 기록될 수 있음
DEPLOY_OK=true
if ! docker compose pull app; then
  echo "이미지 pull 실패" >&2
  DEPLOY_OK=false
fi
if [ "$DEPLOY_OK" = true ] && ! docker compose build nginx; then
  echo "nginx 빌드 실패" >&2
  DEPLOY_OK=false
fi
if [ "$DEPLOY_OK" = true ] && ! docker compose up -d --force-recreate app; then
  echo "compose up 실패" >&2
  DEPLOY_OK=false
fi
# certbot은 이미지 태그나 설정이 바뀐 경우에만 재생성되며, 실패해도 앱 배포 결과와는 무관
if [ "$DEPLOY_OK" = true ] && ! docker compose up -d certbot; then
  echo "certbot 기동 실패 (인증서 자동 갱신 확인 필요)" >&2
fi

if [ "$DEPLOY_OK" = true ]; then
  if wait_app_healthy "app 헬스체크"; then
    echo "app 헬스체크 통과"
  else
    echo "app 헬스체크 실패" >&2
    DEPLOY_OK=false
  fi
else
  echo "배포 명령 실패로 헬스체크 생략, 롤백 절차로 진행" >&2
fi

# nginx 이미지가 바뀐 경우에만 재생성
# 그 외엔 기존 컨테이너를 두고 reload.sh로 설정만 재로드
if [ "$DEPLOY_OK" = true ] && ! docker compose up -d nginx; then
  echo "nginx 기동 실패" >&2
  DEPLOY_OK=false
fi
if [ "$DEPLOY_OK" = true ] && ! docker compose exec -T nginx /usr/local/bin/reload.sh; then
  echo "nginx 설정 재로드 실패" >&2
  DEPLOY_OK=false
fi

if [ "$DEPLOY_OK" = true ]; then
  echo "$IMAGE_TAG" > .deployed_sha
  remove_old_app_images
  docker image prune -f || true
  exit 0
fi

docker compose logs --tail=100 app || true

if [ -z "$PREV_SHA" ]; then
  echo "이전 이미지 정보 없음, 롤백 불가" >&2
  exit 1
fi

if [ ! -f .prev_release/.env ]; then
  echo "이전 .env 스냅샷 없음, 롤백 불가" >&2
  exit 1
fi

echo "이전 이미지(SHA: $PREV_SHA)로 롤백 시도" >&2

# app 이미지뿐 아니라 새로 배포된 compose/nginx/.env 설정도 함께 되돌림
cp .prev_release/docker-compose.yml docker-compose.yml
rm -rf nginx
cp -r .prev_release/nginx nginx
cp -p .prev_release/.env .env

export IMAGE_TAG="$PREV_SHA"
docker compose config >/dev/null

ROLLBACK_OK=true
if ! docker compose pull app; then
  echo "롤백용 이미지 pull 실패" >&2
  ROLLBACK_OK=false
fi
if [ "$ROLLBACK_OK" = true ] && ! docker compose build nginx; then
  echo "롤백용 nginx 빌드 실패" >&2
  ROLLBACK_OK=false
fi
# app을 먼저 확실히 되돌린 뒤에만 nginx를 재생성
if [ "$ROLLBACK_OK" = true ] && ! docker compose up -d --force-recreate app; then
  echo "롤백용 compose up 실패" >&2
  ROLLBACK_OK=false
fi

if [ "$ROLLBACK_OK" = true ] && ! wait_app_healthy "롤백 app 헬스체크"; then
  ROLLBACK_OK=false
fi

if [ "$ROLLBACK_OK" = true ] && ! docker compose up -d --force-recreate nginx; then
  echo "롤백용 nginx 재생성 실패" >&2
  ROLLBACK_OK=false
fi

# Host 헤더 없이 요청하면 default_server(444)로 빠지므로 도메인을 지정해 HTTPS 리다이렉트(301) 확인
ROLLBACK_DOMAIN=$(sed -n 's/^DOMAIN=//p' .env | head -n 1)

if [ "$ROLLBACK_OK" = true ]; then
  for i in $(seq 1 15); do
    if app_healthy \
      && [ "$(curl -s --max-time 5 -o /dev/null -w '%{http_code}' -H "Host: $ROLLBACK_DOMAIN" http://127.0.0.1/)" = "301" ]; then
      echo "롤백 완료, 이전 버전으로 정상 기동"
      exit 1
    fi
    echo "롤백 헬스체크 대기 중... ($i/15)"
    sleep 4
  done
fi

echo "롤백도 실패" >&2
docker compose logs --tail=100 app nginx || true
exit 1
