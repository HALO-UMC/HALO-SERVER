# halo-server 런타임 이미지 (JDK 21, Amazon Corretto AL2023)
FROM amazoncorretto:21-al2023

# 서버 시간대를 KST로 고정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 비루트 계정 생성 (AL2023 미니멀 이미지엔 shadow-utils가 빠져 있어 먼저 설치)
RUN dnf install -y shadow-utils && dnf clean all && \
    groupadd halo && useradd -g halo -M halo

# CI에서 --build-arg 로 실제 jar 경로를 넘겨줄 수 있도록 기본값만 지정
ARG JAR_FILE=build/libs/*.jar

WORKDIR /app

COPY --chown=halo:halo ${JAR_FILE} app.jar

USER halo

EXPOSE 8080
ENV JVM_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxMetaspaceSize=256m -Duser.timezone=Asia/Seoul"

# exec 로 java 를 PID 1 로 만들어 SIGTERM(docker stop)이 graceful shutdown 되게 함
ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar app.jar"]