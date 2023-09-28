FROM ntjp/tomcat:9-jdk11-log4j-ecs

ENV APP_NAME=tak-services

ADD target/${APP_NAME}-*.war ${CATALINA_HOME}/webapps/${APP_NAME}.war
USER ind-app
