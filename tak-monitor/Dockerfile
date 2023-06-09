FROM openjdk:8-oraclelinux8

ENV BASE_DIR=/opt/skltp/ \
    USER=ind-app \
    JARFILE=target/tak-monitor-*.jar

ADD ${JARFILE} ${BASE_DIR}/app.jar
RUN useradd -Ms /bin/bash -b ${BASE_DIR} -u 556559423 ${USER} \
 && chown ${USER}:${USER} -R ${BASE_DIR}

WORKDIR ${BASE_DIR}
USER ${USER}
CMD java ${JAVA_OPTS} -jar app.jar