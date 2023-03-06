FROM tomcat:9-jdk8

ENV APP_NAME=tak-web \
    WARFILE=target/tak-web-3.0.0-SNAPSHOT.war \
    TAK_HOME=/etc/tak

VOLUME $TAK_HOME

ADD $WARFILE $CATALINA_HOME/webapps/$APP_NAME.war
