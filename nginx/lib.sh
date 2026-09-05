FRAGMENTS_DIR=/etc/nginx/fragments
CONF_OUT=/etc/nginx/conf.d/app.conf

render() {
  cat "$FRAGMENTS_DIR/00-redirect.conf.template" \
      "$FRAGMENTS_DIR/${APP_FRAGMENT}" > "${CONF_OUT}.tmp" &&
  envsubst '${DOMAIN} ${DEV_DOMAIN}' < "${CONF_OUT}.tmp" > "${CONF_OUT}.rendered" &&
  mv "${CONF_OUT}.rendered" "$CONF_OUT"
}

# 렌더 후 nginx -t로 검증, $CONF_OUT을 직접 덮어쓰므로 성공/실패만 반환
try_full() {
  render || return 1
  nginx -t >/tmp/nginx-t.log 2>&1
}