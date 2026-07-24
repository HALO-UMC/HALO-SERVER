#!/bin/sh
# nginx를 백그라운드로 띄우고, /etc/letsencrypt 하위 인증서 파일이
# certbot renew로 갱신될 때마다 nginx를 reload한다.
set -e

nginx -g "daemon off;" &
NGINX_PID=$!

trap 'kill -TERM "$NGINX_PID" 2>/dev/null' TERM INT

inotifywait -m -r -e close_write -e create -e moved_to -e delete \
  /etc/letsencrypt/live /etc/letsencrypt/archive 2>/dev/null |
while read -r _; do
  echo "[watch-and-reload] 인증서 변경 감지, nginx reload"
  nginx -s reload
done &
WATCH_PID=$!

wait "$NGINX_PID"
kill "$WATCH_PID" 2>/dev/null || true