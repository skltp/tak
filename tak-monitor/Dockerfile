FROM tomcat:9.0.78-jdk11

ENV APP_NAME=tak-monitor \
    WARFILE=target/tak-monitor-*.war \
    TAK_HOME=/etc/tak

VOLUME $TAK_HOME

COPY setenv.sh $CATALINA_HOME/bin/setenv.sh

ADD log4j2-tomcat.xml ecs-v1.2.0-template.json \
    https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-appserver/2.20.0/log4j-appserver-2.20.0.jar \
    https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.20.0/log4j-api-2.20.0.jar \
    https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.20.0/log4j-core-2.20.0.jar \
    https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-jul/2.20.0/log4j-jul-2.20.0.jar \
    https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-layout-template-json/2.20.0/log4j-layout-template-json-2.20.0.jar \
    $CATALINA_HOME/log4j2/

RUN rm $CATALINA_HOME/conf/logging.properties

ADD $WARFILE $CATALINA_HOME/webapps/$APP_NAME.war
