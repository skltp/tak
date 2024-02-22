ARG DOCKER_REPO=docker.drift.inera.se
FROM ${DOCKER_REPO}/ntjp/tomcat:9-jdk11-log4j-ecs-1 as builder

ENV APP_NAME=tak-services \
    TAK_HOME=/etc/tak

ADD target/${APP_NAME}-*.war ${CATALINA_HOME}/webapps/${APP_NAME}.war
RUN mkdir ${CATALINA_HOME}/webapps/${APP_NAME} -p \
 && cd ${CATALINA_HOME}/webapps/${APP_NAME} \
 && jar xf ${CATALINA_HOME}/webapps/${APP_NAME}.war \
 && rm ${CATALINA_HOME}/webapps/${APP_NAME}.war


FROM ${DOCKER_REPO}/ntjp/tomcat:9-jdk11-log4j-ecs-1
ENV APP_NAME=tak-services \
    TAK_HOME=/etc/tak
COPY --from=builder ${CATALINA_HOME}/webapps/${APP_NAME}/ webapps/${APP_NAME}/
