FROM openjdk:17-alpine:latest

RUN mkdir -p /potm-gateway

WORKDIR /potm-gateway

ARG JAR_FILE=build/libs/potm-gateway-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 9070

ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

CMD sleep 60; java $JAVA_OPTS -jar app.jar
