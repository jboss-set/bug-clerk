#!/bin/bash

readonly BUGCLERK_HOME=${BUGCLERK_HOME:-'.'}
readonly MAIN_CLASS='org.jboss.jbossset.bugclerk.LoadBugsIdsFromBZ'

readonly AUTH_URL="https://bugzilla.redhat.com/index.cgi"
readonly FILTER_URL='https://bugzilla.redhat.com/buglist.cgi?cmdtype=runnamed&namedcmd=EAP_6_LAST_HOUR_CHANGES&ctype=csv&list_id=3184738'

readonly USER=${1}
readonly PASSWORD=${2}

usage() {
  echo "$(basename ${0}) <bugzilla-username> <bugzilla-password>"
  echo ''
}

if [ -z "${USER}" ]; then
  echo 'Missing BZ username.'
  usage
  exit 1
fi

if [ -z "${PASSWORD}" ]; then
  echo 'Missing BZ password.'
  usage
  exit 2
fi

if [ ! -d "${BUGCLERK_HOME}" ]; then
  echo "Invalid BUGCLERK_HOME (not a directory): ${BUGCLERK_HOME}"
  exit 3
fi

if [ ! -e "${BUGCLERK_HOME}"/bugclerk*.jar ]; then
  echo "No jar file in directory: ${BUGCLERK_HOME}."
  exit 4
fi


java -cp ${BUGCLERK_HOME}/*.jar "${MAIN_CLASS}" \
     -h "${AUTH_URL}" \
     -f "${FILTER_URL}" \
     -u "${USERNAME}" -p "${PASSWORD}"
