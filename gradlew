#!/usr/bin/env sh
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR" || exit 1
# Honour GRADLE_OPTS / JAVA_OPTS from environment (CI sets memory)
exec java ${JAVA_OPTS:--Xmx512m} -classpath "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
