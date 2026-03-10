#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.
# Maven Wrapper script for Unix-like systems.

set -euf
[ "${MVNW_VERBOSE-}" != true ] || set -x

# OS specific support
case "$(uname)" in
  CYGWIN* | MINGW* | MSYS*) cygwin=true ;; *) cygwin=false ;;
esac

# Determine MAVEN_HOME from script location
if [ -z "${MAVEN_PROJECTBASEDIR-}" ]; then
  MAVEN_PROJECTBASEDIR=$(cd "$(dirname "$0")" && pwd -P)
  export MAVEN_PROJECTBASEDIR
fi

MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

# Download maven-wrapper.jar if not present
if [ ! -e "$MAVEN_WRAPPER_JAR" ]; then
  if [ "${MVNW_VERBOSE-}" = true ]; then
    echo "Downloading Maven Wrapper..."
  fi

  if command -v wget > /dev/null 2>&1; then
    [ "${MVNW_VERBOSE-}" = true ] && QUIET="" || QUIET="--quiet"
    if [ -f "$MAVEN_WRAPPER_PROPERTIES" ]; then
      WRAPPER_URL=$(grep 'wrapperUrl' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2- | tr -d '[:space:]')
    fi
    wget $QUIET "$WRAPPER_URL" -O "$MAVEN_WRAPPER_JAR" || :
  elif command -v curl > /dev/null 2>&1; then
    [ "${MVNW_VERBOSE-}" = true ] && QUIET="" || QUIET="--silent"
    if [ -f "$MAVEN_WRAPPER_PROPERTIES" ]; then
      WRAPPER_URL=$(grep 'wrapperUrl' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2- | tr -d '[:space:]')
    fi
    curl $QUIET -o "$MAVEN_WRAPPER_JAR" "$WRAPPER_URL" || :
  else
    echo "Error: Cannot download maven-wrapper.jar. Install wget or curl."
    exit 1
  fi
fi

# Run Maven via wrapper JAR
if [ -e "$MAVEN_WRAPPER_JAR" ]; then
  # Determine Java command
  if [ -n "${JAVA_HOME-}" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
  else
    JAVACMD="$(command -v java)" || { echo "Error: JAVA_HOME not set and 'java' not in PATH"; exit 1; }
  fi

  MAVEN_OPTS="${MAVEN_OPTS-}" 

  exec "$JAVACMD" \
    $MAVEN_OPTS \
    ${MAVEN_WRAPPER_OPTS-} \
    "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
    -jar "$MAVEN_WRAPPER_JAR" "$@"
else
  echo "Error: Could not download maven-wrapper.jar"
  echo "You can install Maven manually: https://maven.apache.org/install.html"
  echo "Then run: mvn spring-boot:run"
  exit 1
fi
