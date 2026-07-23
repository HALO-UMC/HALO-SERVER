#!/bin/sh
# 최초 1회만 실행하는 스크립트.
# nginx는 ssl_certificate 파일이 없으면 아예 기동이 안 되므로
# 1) 임시(더미) 인증서로 nginx를 먼저 띄우고
# 2) certbot으로 진짜 인증서를 발급받은 뒤
# 3) nginx를 재시작해서 진짜 인증서를 적용한다.
#
# 사용법: DOMAIN=popit.co.kr EMAIL=me@example.com ./init-letsencrypt.sh

set -e

if [ -z "$DOMAIN" ] || [ -z "$EMAIL" ]; then
  echo "사용법: DOMAIN=your.domain EMAIL=you@example.com ./init-letsencrypt.sh"
  exit 1
fi

echo "### 1. 더미 인증서 생성 ###"
docker compose run --rm --entrypoint "\
  mkdir -p /etc/letsencrypt/live/$DOMAIN && \
  openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
    -keyout /etc/letsencrypt/live/$DOMAIN/privkey.pem \
    -out /etc/letsencrypt/live/$DOMAIN/fullchain.pem \
    -subj '/CN=localhost'" certbot

echo "### 2. nginx 기동 ###"
docker compose up -d nginx

echo "### 3. 더미 인증서 삭제 ###"
docker compose run --rm --entrypoint "\
  rm -rf /etc/letsencrypt/live/$DOMAIN && \
  rm -rf /etc/letsencrypt/archive/$DOMAIN && \
  rm -rf /etc/letsencrypt/renewal/$DOMAIN.conf" certbot

echo "### 4. 실제 인증서 발급 ###"
docker compose run --rm --entrypoint "\
  certbot certonly --webroot -w /var/www/certbot \
    -d $DOMAIN \
    --email $EMAIL --agree-tos --no-eff-email" certbot

echo "### 5. nginx 재시작 ###"
docker compose exec nginx nginx -s reload

echo "완료. https://$DOMAIN 로 접속해보세요."
