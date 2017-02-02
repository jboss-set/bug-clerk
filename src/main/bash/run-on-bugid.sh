#!/bin/bash

readonly APHRODITE_CONFIG=${APHRODITE_CONFIG:-"$(pwd)/aphrodite-config.json"}

readonly BUGCLERK_VERSION=${BUGCLERK_VERSION:-'0.9.0-SNAPSHOT'}
readonly BUGCLERK_HOME=${BUGCLERK_HOME:-'target'}

readonly MAIN_CLASS=${MAIN_CLASS:-'org.jboss.jbossset.bugclerk.cli.BugClerkCLI'}

readonly BUG_ID=${1}

if [ ! -d "${BUGCLERK_HOME}" ]; then
  echo "Invalid BUGCLERK_HOME (not a directory): ${BUGCLERK_HOME}"
  exit 3
fi

if [ -z "${BUGCLERK_VERSION}" ]; then
  echo "BUGCLERK_VERSION has not been defined !${BUGCLERK_VERSION}"
  exit 4
fi

if [ ! -e "${BUGCLERK_HOME}/bugclerk-${BUGCLERK_VERSION}.jar" ]; then
  echo "No jar file in directory: ${BUGCLERK_HOME}."
  exit 4
fi

if [ -z "${BUG_ID}" ]; then
  echo "No bug ID provided."
  exit 1
fi

java -Daphrodite.config=${APHRODITE_CONFIG} \
     -cp "${BUGCLERK_HOME}/bugclerk-${BUGCLERK_VERSION}-shaded.jar" \
     "${MAIN_CLASS}" \
     "${BUG_ID}" ${@}
