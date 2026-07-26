#!/bin/sh
# nginx를 백그라운드로 띄우고, /etc/letsencrypt 하위 인증서 파일이
# certbot renew로 갱신될 때마다 nginx를 reload한다.
set -e

# 직접 치환
envsubst '${DOMAIN} ${DEV_DOMAIN}' < /etc/nginx/templates/app.conf.template > /etc/nginx/conf.d/app.conf

# upstream의 app/dev-app 호스트명은 nginx 기동 시점에 한 번만 DNS로 해석
# prod/dev는 서로 다른 compose profile이라 항상 둘 다 떠 있지 않음
# 늦게 뜨는 쪽을 잠깐 기다렸다가 기동, 없다고 nginx 전체가 기동 실패하는 것을 막음
for i in $(seq 1 30); do
  if nginx -t >/tmp/nginx-t.log 2>&1; then
    break
  fi
  echo "[watch-and-reload] nginx 설정 테스트 실패, app/dev-app 기동 대기 중... ($i/30)"
  sleep 2
done
cat /tmp/nginx-t.log >&2 || true

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