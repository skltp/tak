FROM alpine:3 AS builder

ENV APP_NAME=tak-web \
    NEW_APP_NAME=root \
    TAK_HOME=/etc/tak

ADD target/${APP_NAME}-*.war /webapps/root.war
RUN mkdir /webapps/root -p \
 && unzip /webapps/root.war -d ${CATALINA_HOME}/webapps/root \
 && rm /webapps/root.war

FROM docker.drift.inera.se/ntjp/tomcat:9-jre11-log4j-ecs
USER ${APP_USER}

ENV APP_NAME=tak-web \
    TAK_HOME=/etc/tak
COPY --from=builder /webapps/root/ webapps/root/
