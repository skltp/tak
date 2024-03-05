FROM alpine:3 AS builder

ENV APP_NAME=tak-monitor \
    TAK_HOME=/etc/tak

ADD target/${APP_NAME}-*.war /webapps/${APP_NAME}.war
RUN mkdir /webapps/${APP_NAME} -p \
 && unzip /webapps/${APP_NAME}.war -d /webapps/${APP_NAME}

FROM docker.drift.inera.se/ntjp/tomcat:9-jre11-log4j-ecs
USER ${APP_USER}

ENV APP_NAME=tak-monitor \
    TAK_HOME=/etc/tak
COPY --from=builder /webapps/${APP_NAME}/ webapps/${APP_NAME}/
