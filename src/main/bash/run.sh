#!/bin/bash
readonly BUG_CLERK_HOME=${BUG_CLERK_HOME:-'.'}
readonly JAR_NAME=${JAR_NAME:-'bugclerk'}
readonly VERSION=${VERSION:-'0.2-SNAPSHOT'}
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

if [ ! -e "${BUG_CLERK_HOME}" ]; then
  echo "The BUG_CLERK_HOME '${BUG_CLERK_HOME}' provided does not exist."
  exit 2
fi

java -jar "${BUG_CLERK_HOME}/${JAR_NAME}-${VERSION}.jar" -u "${BZ_SERVER_URL}" "${@}"
