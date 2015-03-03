#!/bin/bash

readonly BUGCLERK_HOME=${BUGCLERK_HOME:-'.'}
readonly BUGCLERK_VERSION=${BUGCLERK_VERSION:-${project.version}}

readonly MAIN_CLASS='org.jboss.jbossset.bugclerk.cli.LoadBugsIdsFromBZ'

readonly BZ_SERVER_URL='https://bugzilla.redhat.com'
readonly AUTH_URL="${BZ_SERVER_URL}/index.cgi"
readonly FILTER_URL=${FILTER_URL:-"${BZ_SERVER_URL}/buglist.cgi?cmdtype=runnamed&namedcmd=EAP_6_LAST_HOUR_CHANGES&ctype=csv&list_id=3184738"}

usage() {
  echo "$(basename ${0})"
  echo ''
}

if [ ! -d "${BUGCLERK_HOME}" ]; then
  echo "Invalid BUGCLERK_HOME (not a directory): ${BUGCLERK_HOME}"
  exit 3
fi

if [ ! -e "${BUGCLERK_HOME}"/bugclerk*.jar ]; then
  echo "No jar file in directory: ${BUGCLERK_HOME}."
  exit 4
fi

cd "${BUGCLERK_HOME}"
java -cp ./bugclerk-${BUGCLERK_VERSION}.jar "${MAIN_CLASS}" \
     -h "${AUTH_URL}" \
     -f "${FILTER_URL}"
cd - > /dev/null
