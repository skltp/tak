FROM tomcat:9-jdk8

ENV APP_NAME=tak-monitor \
    WARFILE=target/tak-monitor-*.war \
    TAK_HOME=/etc/tak

VOLUME $TAK_HOME

ADD $WARFILE $CATALINA_HOME/webapps/$APP_NAME.war
