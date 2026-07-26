#!/bin/sh
# 최초 1회만 실행하는 스크립트.
# nginx는 ssl_certificate 파일이 없으면 아예 기동이 안 되므로
# 1) 임시(더미) 인증서로 nginx를 먼저 띄우고
# 2) certbot으로 진짜 인증서를 발급받은 뒤
# 3) nginx를 재시작해서 진짜 인증서를 적용한다.
#
# 사용법: DOMAIN=your.domain DEV_DOMAIN=dev.your.domain EMAIL=you@example.com ./init-letsencrypt.sh
# DEV_DOMAIN은 docker-compose.yml에서 nginx 서비스에 필수 값으로 요구되므로 여기서도 필수로 검증한다.

set -e

if [ -z "$DOMAIN" ] || [ -z "$DEV_DOMAIN" ] || [ -z "$EMAIL" ]; then
  echo "사용법: DOMAIN=your.domain DEV_DOMAIN=dev.your.domain EMAIL=you@example.com ./init-letsencrypt.sh"
  exit 1
fi

# 같은 인증서의 SAN에 DEV_DOMAIN을 함께 포함해서 발급
DOMAIN_ARGS="-d $DOMAIN -d $DEV_DOMAIN"

echo "### 0. 기존 인증서/갱신 설정 확인 ###"
if docker compose run --rm --entrypoint /bin/sh certbot -ec "
  [ -e /etc/letsencrypt/renewal/$DOMAIN.conf ] || [ -e /etc/letsencrypt/live/$DOMAIN/fullchain.pem ]
"; then
  echo "이미 $DOMAIN 에 대한 인증서 또는 renewal 설정이 존재합니다."
  echo "이 스크립트를 다시 실행하면 기존 인증서가 삭제됩니다. 갱신은 'docker compose run --rm certbot renew'를 사용하고,"
  echo "정말로 재발급이 필요하면 기존 인증서를 백업/삭제한 뒤 다시 실행하세요."
  exit 1
fi

echo "### 1. 더미 인증서 생성 ###"
docker compose run --rm --entrypoint /bin/sh certbot -ec "
  mkdir -p /etc/letsencrypt/live/$DOMAIN &&
  openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
    -keyout /etc/letsencrypt/live/$DOMAIN/privkey.pem \
    -out /etc/letsencrypt/live/$DOMAIN/fullchain.pem \
    -subj /CN=localhost
"

echo "### 2. nginx 기동 ###"
docker compose up -d nginx

echo "### 3. 더미 인증서 삭제 ###"
docker compose run --rm --entrypoint /bin/sh certbot -ec "
  rm -rf /etc/letsencrypt/live/$DOMAIN &&
  rm -rf /etc/letsencrypt/archive/$DOMAIN &&
  rm -rf /etc/letsencrypt/renewal/$DOMAIN.conf
"

echo "### 4. 실제 인증서 발급 ###"
docker compose run --rm --entrypoint /bin/sh certbot -ec "
  certbot certonly --webroot -w /var/www/certbot \
    $DOMAIN_ARGS \
    --email $EMAIL --agree-tos --no-eff-email
"

echo "### 5. nginx 재시작 ###"
docker compose exec nginx nginx -s reload

echo "완료. https://$DOMAIN 로 접속해보세요."
