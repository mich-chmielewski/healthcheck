FROM openjdk:17
MAINTAINER mich


ARG build_username=user
ARG build_password=user

ENV USER_NAME=$build_username
ENV USER_PASS=$build_password
WORKDIR /srv

COPY healthcheck-0.0.1-SNAPSHOT.jar /srv/healthcheck-0.0.1-SNAPSHOT.jar

CMD ["java","-jar","/srv/healthcheck-0.0.1-SNAPSHOT.jar"]
