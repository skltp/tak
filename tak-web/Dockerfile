FROM eclipse-temurin:17-jre-alpine

ENV APP_NAME=tak-web \
    TAK_HOME=/etc/tak \
    BASE_DIR=/opt/skltp/ \
    USER=ind-app \
    LOG_DIR=/var/log/ \
    JARFILE=target/tak-*.jar

RUN mkdir -p ${LOG_DIR} ${BASE_DIR} \
 && adduser -HD -u 1000 -h ${BASE_DIR} ${USER} \
 && chown ${USER}:${USER} -R ${BASE_DIR}

ADD ${JARFILE} ${BASE_DIR}/app.jar

WORKDIR ${BASE_DIR}
USER ${USER}
CMD java ${JAVA_OPTS} -jar app.jar
