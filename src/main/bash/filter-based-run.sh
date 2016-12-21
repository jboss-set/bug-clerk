#!/bin/bash

readonly TRACKER_USERNAME=${TRACKER_USERNAME}
readonly TRACKER_PASSWORD=${TRACKER_PASSWORD}
readonly TRACKER_TYPE=${TRACKER_TYPE:-'jira'}

readonly BUGCLERK_HOME=${BUGCLERK_HOME:-'.'}
readonly BUGCLERK_VERSION=${BUGCLERK_VERSION:-${project.version}}
readonly JAR_NAME=${JAR_NAME:-'bugclerk'}

readonly BZ_SERVER_URL='https://bugzilla.redhat.com'

readonly FILTER_ID=${FILTER_ID:-'12330472'}
readonly FILTER_URL_ENDPOINT='https://issues.jboss.org/rest/api/latest/filter'
readonly FILTER_URL=${FILTER_URL:-${FILTER_URL_ENDPOINT}/${FILTER_ID}}

usage() {
  echo "$(basename ${0})"
  echo ''
}

if [ -z "${TRACKER_USERNAME}" ]; then
  echo "Missing Bugzilla Username"
  exit 1
fi

if [ -z "${TRACKER_PASSWORD}" ]; then
  echo "Missing Bugzilla Password"
  exit 2
fi

if [ ! -d "${BUGCLERK_HOME}" ]; then
  echo "Invalid BUGCLERK_HOME (not a directory): ${BUGCLERK_HOME}"
  exit 3
fi

if [ -z "${BUGCLERK_VERSION}" ]; then
  echo "BUGCLERK_VERSION has not been defined !${BUGCLERK_VERSION}"
  exit 4
fi

if [ ! -e "${BUGCLERK_HOME}/${JAR_NAME}-${BUGCLERK_VERSION}.jar" ]; then
  echo "No jar file in directory: ${BUGCLERK_HOME}."
  exit 4
fi

cd "${BUGCLERK_HOME}"
java -jar ./bugclerk-${BUGCLERK_VERSION}-shaded.jar \
     -f "${FILTER_URL}" \
     -t "${TRACKER_TYPE}"\
     -u "${TRACKER_USERNAME}" \
     -p "${TRACKER_PASSWORD}" \
     ${@}
readonly STATUS=${?}
cd - > /dev/null
exit ${STATUS}
