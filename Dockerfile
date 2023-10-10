FROM maven:3-ibm-semeru-11-focal as maven

WORKDIR /opt/build

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=bind,source=.git,target=.git \
    --mount=type=bind,source=report,target=report \
    --mount=type=bind,source=resources,target=resources \
    --mount=type=bind,source=tak-core,target=tak-core \
    --mount=type=bind,source=tak-integration,target=tak-integration \
    --mount=type=bind,source=tak-monitor,target=tak-monitor \
    --mount=type=bind,source=tak-schemas,target=tak-schemas \
    --mount=type=bind,source=tak-services,target=tak-services \
    --mount=type=bind,source=tak-tests,target=tak-tests \
    --mount=type=bind,source=tak-web,target=tak-web \
    --mount=type=cache,target=/root/.m2 \
     ls -la && mvn install -PaltDir -DskipTests=true


FROM ibm-semeru-runtimes:open-11-jre as tak-monitor

ENV TAK_APP=monitor \
    LOGGING_CONFIG=/opt/app/log4j2.xml
RUN mkdir /opt/app && useradd ind-app
COPY --from=maven /opt/targets/${TAK_APP}/*.jar /opt/app/app.jar
COPY log4j2.xml $LOGGING_CONFIG
CMD ["java", "-jar", "/opt/app/app.jar"]
USER ind-app

FROM ibm-semeru-runtimes:open-11-jre as tak-services

ENV TAK_APP=services \
    LOGGING_CONFIG=/opt/app/log4j2.xml
RUN mkdir /opt/app && useradd ind-app
COPY --from=maven /opt/targets/${TAK_APP}/*.jar /opt/app/app.jar
COPY log4j2.xml $LOGGING_CONFIG
CMD ["java", "-jar", "/opt/app/app.jar"]
USER ind-app

FROM ibm-semeru-runtimes:open-11-jre as tak-web

ENV TAK_APP=web \
    LOGGING_CONFIG=/opt/app/log4j2.xml
RUN mkdir /opt/app && useradd ind-app
COPY --from=maven /opt/targets/${TAK_APP}/*.jar /opt/app/app.jar
COPY log4j2.xml $LOGGING_CONFIG
CMD ["java", "-jar", "/opt/app/app.jar"]
USER ind-app
