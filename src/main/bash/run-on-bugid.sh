#!/bin/bash

readonly BUGCLERK_VERSION=${BUGCLERK_VERSION:-'0.5.1.Final'}
readonly BUGCLERK_HOME=${BUGCLERK_HOME:-'target'}
readonly MAIN_CLASS=${MAIN_CLASS:-'org.jboss.jbossset.bugclerk.cli.BugClerkCLI'}
readonly URL_PREFIX=${URL_PREFIX:-'https://bugzilla.redhat.com/show_bug.cgi?id='}

readonly BUG_ID=${1}
shift

if [ -z "${BUG_ID}" ]; then
  echo "No bug ID provided."
  exit 1
fi

java -cp "${BUGCLERK_HOME}/bugclerk-${BUGCLERK_VERSION}.jar" "${MAIN_CLASS}" \
     -u "${URL_PREFIX}"  "${BUG_ID}" ${@}
