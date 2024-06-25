FROM alpine:3 AS builder

ENV APP_NAME=tak-web \
    TAK_HOME=/etc/tak

ADD target/${APP_NAME}-*.war /webapps/root.war

FROM docker.drift.inera.se/ntjp/tomcat:9-jre11-log4j-ecs
USER ${APP_USER}

ENV APP_NAME=tak-web \
    TAK_HOME=/etc/tak
COPY --from=builder /webapps/ webapps/
