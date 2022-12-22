FROM groovy:3.0.13-jdk8

USER root
RUN apt update && apt install -y openssh-client
USER groovy

COPY --chown=groovy:groovy export/*.groovy export/
COPY --chown=groovy:groovy export/*.sh export/

RUN chmod +x export/*.sh \
 && groovy export/grabit.groovy
