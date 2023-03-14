FROM tomcat:9-jdk8

ENV APP_NAME=tak-services \
    WARFILE=target/tak-services-*.war

ADD $WARFILE $CATALINA_HOME/webapps/$APP_NAME.war
