FROM tomcat:9-jdk8

ENV APP_NAME=tak-services \
    TAK_BACKGROUND=#aaffaa \
    TAK_DATABASE_PORT=3306 \
    TAK_DATABASE_SCHEME=takv3 \
    TAK_DATABASE_USERNAME=tpadminuser \
    TAK_ENVIRONMENT=BKSDEV \
    TAK_HOME=$CATALINA_HOME/etc \
    WARFILE=target/tak-services-2.5.2-SNAPSHOT.war

ADD docker/catalina_home/ $CATALINA_HOME
ADD $WARFILE $CATALINA_HOME/webapps/$APP_NAME.war

RUN curl https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.30/mysql-connector-java-8.0.30.jar -o lib/mysql-connector-java.jar \
 && curl https://repo1.maven.org/maven2/javax/mail/javax.mail-api/1.6.2/javax.mail-api-1.6.2.jar -o lib/javax.mail-api.jar \
 && curl https://repo1.maven.org/maven2/com/sun/mail/javax.mail/1.6.2/javax.mail-1.6.2.jar -o lib/javax.mail.jar \
 && curl https://repo1.maven.org/maven2/com/sun/activation/javax.activation/1.2.0/javax.activation-1.2.0.jar -o lib/javax.activation.jar