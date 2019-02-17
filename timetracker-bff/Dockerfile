FROM openjdk:8-jdk-alpine

MAINTAINER alisanyilmaz@icloud.com

RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*

VOLUME /tmp

ARG DEPENDENCY=target/dependency

COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","com.ayilmaz.timetracker.timetrackerbff.TimetrackerBffApplication"]

EXPOSE 8080
