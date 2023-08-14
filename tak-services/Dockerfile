FROM tomcat:9.0.78-jdk11

ENV APP_NAME=tak-services \
    WARFILE=target/tak-services-*.war

ADD $WARFILE $CATALINA_HOME/webapps/$APP_NAME.war
