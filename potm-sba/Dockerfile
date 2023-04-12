FROM openjdk:17-alpine:latest

RUN mkdir -p /potm-sba

WORKDIR /potm-sba

ARG JAR_FILE=build/libs/potm-sba-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 9060

ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

CMD sleep 60; java $JAVA_OPTS -jar app.jar
