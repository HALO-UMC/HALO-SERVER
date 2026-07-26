#!/bin/sh
# nginx를 백그라운드로 띄우고, /etc/letsencrypt 하위 인증서 파일이
# certbot renew로 갱신될 때마다 nginx를 reload한다.
set -e
. /usr/local/bin/nginx-lib.sh

# app/dev-app이 동시에 뜨는 중이라 아직 DNS가 안 잡힌 것뿐일 수 있으니
# 먼저 충분히(최대 60초) 재시도한다 — 정상적인 동시 기동 레이스 컨디션 대비
NGINX_OK=false
for i in $(seq 1 30); do
  if try_full; then
    NGINX_OK=true
    break
  fi
  echo "[watch-and-reload] nginx 설정 테스트 실패, app/dev-app 기동 대기 중... ($i/30)"
  cat /tmp/nginx-t.log >&2 || true
  sleep 2
done

# 그래도 안 되면(둘 중 하나가 이번 배포 대상이 아니라 아예 없는 경우) 존재하는
# 백엔드만으로 다시 구성한다. 그마저 실패하면 정말 설정 문제이므로 그대로 죽는다.
if [ "$NGINX_OK" != true ]; then
  echo "[watch-and-reload] 대기 시간 초과, 존재하는 백엔드만으로 재구성" >&2
  try_partial
fi

nginx -g "daemon off;" &
NGINX_PID=$!

trap 'kill -TERM "$NGINX_PID" 2>/dev/null' TERM INT

watch_and_reload() {
  while true; do
    inotifywait -m -r -e close_write -e create -e moved_to \
      /etc/letsencrypt/live /etc/letsencrypt/archive 2>/dev/null |
    while read -r _; do
      echo "[watch-and-reload] 인증서 변경 감지, nginx reload"
      nginx -s reload || echo "[watch-and-reload] reload 실패, 다음 변경 시 재시도"
    done
    echo "[watch-and-reload] inotifywait 종료됨, 재시작"
    sleep 1
  done
}

watch_and_reload &
WATCH_PID=$!

wait "$NGINX_PID"
kill "$WATCH_PID" 2>/dev/null || true