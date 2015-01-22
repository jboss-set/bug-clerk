#!/bin/bash

readonly BZ_URL=${BZ_URL:-'https://bugzilla.redhat.com/buglist.cgi'}
readonly CLASSIFICATION=${CLASSIFICATION:-'JBoss'}

readonly URL="${BZ_URL}?${CLASSIFICATION}&columnlist=product%2Ccomponent%2Cassigned_to%2Cbug_status%2Cpriority%2Cbug_severity%2Cresolution%2Cshort_desc%2Cchangeddate%2Cflagtypes.name&priority=urgent&priority=high&priority=medium&priority=low&product=JBoss%20Enterprise%20Application%20Platform%206&query_format=advanced&title=Bug%20List%3A%20Potential%20JBoss%20SET%20EAP%206%20issues&ctype=atom&list_id=3176545"

readonly FEED_FILE=$(mktemp)

readonly BUG_CLERK_HOME=${BUG_CLERK_HOME:-'.'}
readonly JAR_NAME=${JAR_NAME:-'bugclerk'}
readonly VERSION=${VERSION:-'0.1'}

readonly BZ_SERVER_URL=${BZ_SERVER_URL:-'https://bugzilla.redhat.com/show_bug.cgi?id='}

checkScriptDependency() {
  local cmd=${1}

  which ${cmd} > /dev/null
  if [ "${?}" -ne 0 ]; then
    echo "This scripts requires command '${cmd}' which is missing from PATH."
    exit 1
  fi
}

checkScriptDependency wget
checkScriptDependency java

if [ ! -e "${BUG_CLERK_HOME}" ]; then
  echo "The BUG_CLERK_HOME '${BUG_CLERK_HOME}' provided does not exist."
  exit 2
fi

echo "Downloading an storing feed in file ${FEED_FILE}."
wget -q --output-document="${FEED_FILE}" "${URL}"
echo -n "Extracting Bug IDs... "
readonly BUGS_ID=$(cat "${FEED_FILE}" | grep -e '<id>' | grep -e 'show_bug'| sed -e 's;^.*<id>.*id=;;' -e 's;</id>.*$;;' -e 's/\n/ /g')
echo 'Done.'

echo "Running ${JAR_NAME}-${VERSION} from ${BUG_CLERK_HOME}:"
java -jar "${BUG_CLERK_HOME}/${JAR_NAME}-${VERSION}.jar" -u "${BZ_SERVER_URL}" ${BUGS_ID}
