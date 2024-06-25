FROM alpine:3 AS builder

ENV APP_NAME=tak-web \
    TAK_HOME=/etc/tak

ADD target/${APP_NAME}-*.war /webapps/${APP_NAME}.war
RUN mkdir /webapps/root -p \
 && unzip /webapps/${APP_NAME}.war -d /webapps/root/ \
 && rm /webapps/${APP_NAME}.war

FROM docker.drift.inera.se/ntjp/tomcat:9-jre11-log4j-ecs
USER ${APP_USER}

ENV APP_NAME=tak-web \
    TAK_HOME=/etc/tak
COPY --from=builder /webapps/root/ webapps/root/
