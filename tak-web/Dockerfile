FROM alpine AS builder

ENV APP_NAME=tak-web \
    TAK_HOME=/etc/tak

ADD target/${APP_NAME}-*.war /webapps/${APP_NAME}.war
RUN mkdir /webapps/${APP_NAME} -p \
 && unzip /webapps/${APP_NAME}.war -d ${CATALINA_HOME}/webapps/${APP_NAME}\
 && rm /webapps/${APP_NAME}.war

ARG DOCKER_REPO=docker.drift.inera.se
FROM ${DOCKER_REPO}/ntjp/tomcat:9-jdk11-log4j-ecs
ENV APP_NAME=tak-web \
    TAK_HOME=/etc/tak
COPY --from=builder /webapps/${APP_NAME}/ webapps/${APP_NAME}/
