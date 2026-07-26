#!/bin/sh
# nginx를 백그라운드로 띄우고, /etc/letsencrypt 하위 인증서 파일이
# certbot renew로 갱신될 때마다 nginx를 reload한다.
set -e

FRAGMENTS_DIR=/etc/nginx/fragments
CONF_OUT=/etc/nginx/conf.d/app.conf

# app/dev-app이 실제로 해석되는지 nginx 자신으로 확인한다(getent 등 alpine에
# 없을 수 있는 외부 도구에 기대지 않기 위함). resolve_check라는 별도 upstream만
# 담은 최소 설정으로 -t를 돌려서, 성공하면 해당 호스트가 존재하는 것으로 판단한다.
resolves() {
  host="$1"
  cat > /tmp/resolve-check.conf << RESOLVEEOF
events {}
http {
  upstream resolve_check { server ${host}:8080; }
}
RESOLVEEOF
  nginx -t -c /tmp/resolve-check.conf >/dev/null 2>&1
}

render_full_conf() {
  cat "$FRAGMENTS_DIR/00-redirect.conf.template" \
      "$FRAGMENTS_DIR/10-app.conf.template" \
      "$FRAGMENTS_DIR/20-dev-app.conf.template" > "$CONF_OUT"
  envsubst '${DOMAIN} ${DEV_DOMAIN}' < "$CONF_OUT" > "${CONF_OUT}.rendered"
  mv "${CONF_OUT}.rendered" "$CONF_OUT"
}

# app/dev-app 중 실제로 떠 있는(해석되는) 쪽의 server 블록만 담아서, 없는 쪽
# 때문에 nginx 자체가 기동 실패하는 것을 막는다. prod/dev가 항상 같이 배포되는
# 게 아니라서(compose profile 분리) 최초 부트스트랩 시 한쪽만 있을 수 있음.
render_partial_conf() {
  : > "$CONF_OUT"
  cat "$FRAGMENTS_DIR/00-redirect.conf.template" >> "$CONF_OUT"

  if resolves app; then
    cat "$FRAGMENTS_DIR/10-app.conf.template" >> "$CONF_OUT"
  else
    echo "[watch-and-reload] app 미해석, DOMAIN server 블록 생략"
  fi

  if resolves dev-app; then
    cat "$FRAGMENTS_DIR/20-dev-app.conf.template" >> "$CONF_OUT"
  else
    echo "[watch-and-reload] dev-app 미해석, DEV_DOMAIN server 블록 생략"
  fi

  envsubst '${DOMAIN} ${DEV_DOMAIN}' < "$CONF_OUT" > "${CONF_OUT}.rendered"
  mv "${CONF_OUT}.rendered" "$CONF_OUT"
}

render_full_conf

# app/dev-app이 동시에 뜨는 중이라 아직 DNS가 안 잡힌 것뿐일 수 있으니
# 먼저 충분히(최대 60초) 재시도한다 — 정상적인 동시 기동 레이스 컨디션 대비
NGINX_OK=false
for i in $(seq 1 30); do
  if nginx -t >/tmp/nginx-t.log 2>&1; then
    NGINX_OK=true
    break
  fi
  echo "[watch-and-reload] nginx 설정 테스트 실패, app/dev-app 기동 대기 중... ($i/30)"
  sleep 2
done

# 그래도 안 되면(둘 중 하나가 이번 배포 대상이 아니라 아예 없는 경우) 존재하는
# 백엔드만으로 다시 구성한다. 그마저 실패하면 정말 설정 문제이므로 그대로 죽는다.
if [ "$NGINX_OK" != true ]; then
  echo "[watch-and-reload] 대기 시간 초과, 존재하는 백엔드만으로 재구성" >&2
  cat /tmp/nginx-t.log >&2 || true
  render_partial_conf
  nginx -t
fi

# CD가 fragments/.env 변경만으로 컨테이너를 통째로 재생성하지 않고도 반영
# SIGHUP을 받으면 설정을 다시 렌더링하고 reload
reconfigure_and_reload() {
  render_full_conf
  if nginx -t >/tmp/nginx-t.log 2>&1; then
    nginx -s reload
    echo "[watch-and-reload] 설정 재로드 완료"
    return
  fi
  echo "[watch-and-reload] 전체 설정 테스트 실패, 존재하는 백엔드만으로 재구성 시도" >&2
  cat /tmp/nginx-t.log >&2 || true
  render_partial_conf
  if nginx -t >/tmp/nginx-t.log 2>&1; then
    nginx -s reload
    echo "[watch-and-reload] 부분 설정으로 재로드 완료"
  else
    echo "[watch-and-reload] 설정 재구성 실패, 기존 설정 유지" >&2
    cat /tmp/nginx-t.log >&2 || true
  fi
}

nginx -g "daemon off;" &
NGINX_PID=$!

trap 'kill -TERM "$NGINX_PID" 2>/dev/null' TERM INT
trap 'reconfigure_and_reload' HUP

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

# HUP 트랩 처리로 wait가 조기 반환돼도
# nginx가 실제로 죽은 게 아니면 다시 wait
while kill -0 "$NGINX_PID" 2>/dev/null; do
  wait "$NGINX_PID" 2>/dev/null || true
done
kill "$WATCH_PID" 2>/dev/null || true