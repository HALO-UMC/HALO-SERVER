# certbot이 갱신한 인증서를 nginx가 자동으로 반영(reload)하기 위한 커스텀 이미지
FROM nginx:alpine

RUN apk add --no-cache inotify-tools

COPY lib.sh /usr/local/bin/nginx-lib.sh
COPY watch-and-reload.sh /usr/local/bin/watch-and-reload.sh
COPY reload.sh /usr/local/bin/reload.sh
RUN chmod +x /usr/local/bin/watch-and-reload.sh /usr/local/bin/reload.sh

# nginx 기본 docker-entrypoint.sh가 템플릿(envsubst) 처리를 끝낸 뒤 이 CMD를 실행한다
CMD ["/usr/local/bin/watch-and-reload.sh"]