# halo-server 런타임 이미지 (JDK 21, Amazon Corretto AL2023)
FROM amazoncorretto:21-al2023

# 서버 시간대를 KST로 고정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 비루트 계정 생성 (AL2023 미니멀 이미지엔 shadow-utils가 빠져 있어 먼저 설치)
# curl-minimal: docker-compose healthcheck에서 /actuator/health 호출용
RUN dnf install -y shadow-utils curl-minimal && dnf clean all && \
    groupadd halo && useradd -g halo -M halo

# bootJar.archiveFileName이 app.jar로 고정돼 있어 와일드카드 없이 단일 파일을 지정
ARG JAR_FILE=build/libs/app.jar

WORKDIR /app

COPY --chown=halo:halo ${JAR_FILE} app.jar

USER halo

EXPOSE 8080
ENV JVM_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxMetaspaceSize=256m -Duser.timezone=Asia/Seoul"

# exec 로 java 를 PID 1 로 만들어 SIGTERM(docker stop)이 graceful shutdown 되게 함
ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar app.jar"]