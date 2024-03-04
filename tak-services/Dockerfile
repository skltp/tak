FROM alpine AS builder

ARG DOCKER_REPO=docker.drift.inera.se
ARG APP_NAME=tak-services

ADD target/${APP_NAME}-*.war ${CATALINA_HOME}/webapps/${APP_NAME}.war
RUN mkdir /webapps/${APP_NAME} -p \
 && unzip /webapps/${APP_NAME}.war -d /webapps/${APP_NAME}

FROM ${DOCKER_REPO}ntjp/tomcat:9-jre11-log4j-ecs
ENV APP_NAME=tak-services \
    TAK_HOME=/etc/tak

COPY --from=builder /webapps/${APP_NAME}/ webapps/${APP_NAME}/
