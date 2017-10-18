#!/bin/bash

readonly APHRODITE_CONFIG=${APHRODITE_CONFIG:-"$(pwd)/aphrodite-config.json"}

readonly BUGCLERK_HOME=${BUGCLERK_HOME:-'.'}
readonly BUGCLERK_VERSION=${BUGCLERK_VERSION:-${project.version}}
readonly JAR_NAME=${JAR_NAME:-'bugclerk-dist'}

readonly FILTER_ID=${FILTER_ID:-'12330472'}
readonly FILTER_URL_ENDPOINT='https://issues.jboss.org/rest/api/latest/filter'
readonly FILTER_URL=${FILTER_URL:-${FILTER_URL_ENDPOINT}/${FILTER_ID}}

usage() {
  echo "$(basename ${0})"
  echo ''
}

if [ ! -d "${BUGCLERK_HOME}" ]; then
  echo "Invalid BUGCLERK_HOME (not a directory): ${BUGCLERK_HOME}"
  exit 1
fi

if [ -z "${BUGCLERK_VERSION}" ]; then
  echo "BUGCLERK_VERSION has not been defined !${BUGCLERK_VERSION}"
  exit 2
fi

readonly FULL_PATH_TO_JAR="${BUGCLERK_HOME}/${JAR_NAME}-${BUGCLERK_VERSION}-shaded.jar"
if [ ! -e "${FULL_PATH_TO_JAR}" ]; then
  echo "No jar file in directory: ${FULL_PATH_TO_JAR}."
  exit 3
fi

cd "${BUGCLERK_HOME}"
java ${JAVA_OPTS} -Daphrodite.config="${APHRODITE_CONFIG}" \
     -jar ${FULL_PATH_TO_JAR} \
     -f "${FILTER_URL}" \
     ${@}
readonly STATUS=${?}
cd - > /dev/null
exit ${STATUS}
