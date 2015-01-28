#!/bin/bash
readonly BUGCLERK_HOME=${BUGCLERK_HOME:-'.'}
readonly JAR_NAME=${JAR_NAME:-'bugclerk'}
readonly VERSION=${project.version}
readonly BZ_SERVER_URL=${BZ_SERVER_URL:-'https://bugzilla.redhat.com/show_bug.cgi?id='}

checkScriptDependency() {
  local cmd=${1}

  which ${cmd} > /dev/null
  if [ "${?}" -ne 0 ]; then
    echo "This scripts requires command '${cmd}' which is missing from PATH."
    exit 1
  fi
}

checkScriptDependency java

if [ ! -e "${BUGCLERK_HOME}" ]; then
  echo "The BUGCLERK_HOME '${BUGCLERK_HOME}' provided does not exist."
  exit 2
fi

java -jar "${BUGCLERK_HOME}/${JAR_NAME}-${VERSION}.jar" -u "${BZ_SERVER_URL}" "${@}"
