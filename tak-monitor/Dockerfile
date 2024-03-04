FROM alpine AS builder

ENV APP_NAME=tak-monitor \
    TAK_HOME=/etc/tak

ADD target/${APP_NAME}-*.war /webapps/${APP_NAME}.war
RUN mkdir /webapps/${APP_NAME} -p \
 && unzip /webapps/${APP_NAME}.war -d /webapps/${APP_NAME}

ARG DOCKER_REPO=docker.drift.inera.se/
FROM ${DOCKER_REPO}ntjp/tomcat:9-jre11-log4j-ecs
ENV APP_NAME=tak-monitor \
    TAK_HOME=/etc/tak
COPY --from=builder /webapps/${APP_NAME}/ webapps/${APP_NAME}/
