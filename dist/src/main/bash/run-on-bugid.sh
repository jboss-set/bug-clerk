#!/bin/bash

readonly APHRODITE_CONFIG=${APHRODITE_CONFIG:-"$(pwd)/aphrodite-config.json"}

readonly BUGCLERK_HOME=${BUGCLERK_HOME:-'.'}
readonly BUGCLERK_VERSION=${BUGCLERK_VERSION:-${project.version}}
readonly JAR_NAME=${JAR_NAME:-'bugclerk-dist'}

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

readonly FULL_PATH_TO_JAR="${BUGCLERK_HOME}/${JAR_NAME}-${BUGCLERK_VERSION}-shaded.jar"
if [ ! -e "${FULL_PATH_TO_JAR}" ]; then
  echo "No jar file in directory: ${FULL_PATH_TO_JAR}."
  exit 4
fi

if [ -z "${BUG_ID}" ]; then
  echo "No bug ID provided."
  exit 1
fi

java -Daphrodite.config=${APHRODITE_CONFIG} \
     -cp "${FULL_PATH_TO_JAR}" \
     "${MAIN_CLASS}" \
     "${BUG_ID}" ${@}
