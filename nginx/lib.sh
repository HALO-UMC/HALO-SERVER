# watch-and-reload.sh(기동 시)와 reload.sh(CD가 트리거하는 재구성)가
# 공통으로 쓰는 nginx conf.d/app.conf 렌더링 로직.

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
    echo "[nginx] app 미해석, DOMAIN server 블록 생략" >&2
  fi

  if resolves dev-app; then
    cat "$FRAGMENTS_DIR/20-dev-app.conf.template" >> "$CONF_OUT"
  else
    echo "[nginx] dev-app 미해석, DEV_DOMAIN server 블록 생략" >&2
  fi

  envsubst '${DOMAIN} ${DEV_DOMAIN}' < "$CONF_OUT" > "${CONF_OUT}.rendered"
  mv "${CONF_OUT}.rendered" "$CONF_OUT"
}

# 렌더 후 nginx -t로 검증까지 하는 래퍼. $CONF_OUT을 직접 덮어쓰므로, 호출부가
# 실패 시 디스크 상태를 복구할지 판단할 수 있도록 성공/실패만 반환한다.
try_full() {
  render_full_conf
  nginx -t >/tmp/nginx-t.log 2>&1
}

try_partial() {
  render_partial_conf
  nginx -t >/tmp/nginx-t.log 2>&1
}