#!/bin/sh
# nginx를 백그라운드로 띄우고, 인증서 파일이 certbot renew로 갱신될 때마다 nginx를 reload
set -e
. /usr/local/bin/nginx-lib.sh

NGINX_OK=false
for i in $(seq 1 30); do
  if try_full; then
    NGINX_OK=true
    break
  fi
  echo "[watch-and-reload] nginx 설정 테스트 실패, app 기동 대기 중... ($i/30)"
  cat /tmp/nginx-t.log >&2 || true
  sleep 2
done

if [ "$NGINX_OK" != true ]; then
  echo "[watch-and-reload] 설정 렌더링 실패, 종료" >&2
  cat /tmp/nginx-t.log >&2 || true
  exit 1
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