# FROM openjdk:17-alpine:latest
FROM apache/skywalking-java-agent:8.14.0-java17

RUN mkdir -p /potm-service

WORKDIR /potm-service

ARG JAR_FILE=../../potm-service/build/libs/potm-service-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 9071

ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

CMD sleep 60; java $JAVA_OPTS -jar app.jar
