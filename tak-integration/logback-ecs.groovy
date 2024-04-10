#!/usr/bin/env groovy

@Grapes([
        @Grab(group = 'co.elastic.logging', module = 'logback-ecs-encoder', version = '1.5.0'),
])

import co.elastic.logging.logback.EcsEncoder

appender("STDOUT", ConsoleAppender) {
    encoder(EcsEncoder) {}
}

logger("org.hibernate", ERROR)
logger("org.hibernate.cache", ERROR)

root(DEBUG, ["STDOUT"])
