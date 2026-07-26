#!/bin/sh
# CD가 nginx 컨테이너를 재생성하지 않고 fragments/.env 변경만 반영할 때 쓰는
# 스크립트. `docker compose exec`로 동기 호출되므로, 여기서 실패하면 그 종료
# 코드가 그대로 CD의 성공/실패 판단(및 스냅샷 롤백 트리거)으로 이어진다.
set -e
. /usr/local/bin/nginx-lib.sh

# 재구성 실패 시 복구할 수 있도록 직전 정상 설정을 백업해둔다
CONF_BAK=""
if [ -f "$CONF_OUT" ]; then
  CONF_BAK="${CONF_OUT}.bak"
  cp -f "$CONF_OUT" "$CONF_BAK"
fi

if try_full || try_partial; then
  # 컨테이너가 방금 재생성된 직후라면 watch-and-reload.sh가 아직 최초 기동
  # 대기 루프(최대 60초) 중이라 nginx 마스터가 안 떠 있을 수 있음
  # pid 파일이 생길 때까지 잠깐 기다렸다 reload
  i=0
  while [ ! -f /var/run/nginx.pid ] && [ "$i" -lt 30 ]; do
    sleep 2
    i=$((i + 1))
  done
  nginx -s reload
  echo "[reload] 설정 재로드 완료"
  exit 0
fi

echo "[reload] 설정 재구성 실패" >&2
cat /tmp/nginx-t.log >&2 || true

if [ -n "$CONF_BAK" ]; then
  cp -f "$CONF_BAK" "$CONF_OUT"
  echo "[reload] 직전 정상 설정으로 복구, 재로드는 생략(기존 워커가 계속 서빙)" >&2
fi

exit 1