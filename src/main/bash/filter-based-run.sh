#!/bin/bash

readonly BUGCLERK_HOME=${BUGCLERK_HOME:-'.'}
readonly BUGCLERK_VERSION=${BUGCLERK_VERSION:-${project.version}}
readonly JAR_NAME=${JAR_NAME:-'bugclerk'}

readonly BZ_SERVER_URL='https://bugzilla.redhat.com'
readonly AUTH_URL="${BZ_SERVER_URL}/index.cgi"
readonly FILTER_URL=${FILTER_URL:-"${BZ_SERVER_URL}/buglist.cgi?cmdtype=dorem&list_id=3303150&namedcmd=jboss-eap-6.4.z-superset&remaction=run&sharer_id=213224&ctype=csv"}

usage() {
  echo "$(basename ${0})"
  echo ''
}

if [ ! -d "${BUGCLERK_HOME}" ]; then
  echo "Invalid BUGCLERK_HOME (not a directory): ${BUGCLERK_HOME}"
  exit 3
fi

if [ ! -e "${BUGCLERK_HOME}/${JAR_NAME}-${BUGCLERK_VERSION}.jar" ]; then
  echo "No jar file in directory: ${BUGCLERK_HOME}."
  exit 4
fi

cd "${BUGCLERK_HOME}"
java -jar ./bugclerk-${BUGCLERK_VERSION}.jar \
     -H "${AUTH_URL}" \
     -f "${FILTER_URL}"
readonly STATUS=${?}
cd - > /dev/null
exit ${STATUS}
