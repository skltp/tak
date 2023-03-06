FROM tomcat:9-jdk8

ENV APP_NAME=tak-services \
    WARFILE=target/tak-services-3.0.0-SNAPSHOT.war

ADD docker/catalina_home/ $CATALINA_HOME
ADD $WARFILE $CATALINA_HOME/webapps/$APP_NAME.war
