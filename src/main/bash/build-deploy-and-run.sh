#!/bin/bash

set -e

export MVN_DIR_NAME='apache-maven-3.2.5'
export MVN_URL=${MVN_URL:-"http://mirror.netcologne.de/apache.org/maven/maven-3/3.2.5/binaries/${MVN_DIR_NAME}-bin.tar.gz"}
echo -n "Installing mvn from ${MVN_URL}..."
readonly MVN_ARCHIVE=$(echo ${MVN_URL} | sed -e 's;^.*/;;')
wget "${MVN_URL}" -O "${MVN_ARCHIVE}" 2> /dev/null
tar xvzf "${MVN_ARCHIVE}" > /dev/null
export MAVEN_HOME="$(pwd)/apache-maven-3.2.5"
echo 'Done'.
echo "Maven Home is : ${MAVEN_HOME}."

${MAVEN_HOME}/bin/mvn clean package

export BUGCLERK_HOME=$(src/main/bash/deploy-bugclerk.sh)
export BUGCLERK_VERSION=$(grep -e '^  <version' pom.xml | sed -e 's/^ *<version>//' -e 's;</version>;;')
export FILTER_URL='https://bugzilla.redhat.com/buglist.cgi?cmdtype=dorem&remaction=run&namedcmd=jboss-eap-6.4.z-superset&sharer_id=213224&ctype=csv'

if [ ! -d "${BUGCLERK_HOME}" ]; then
  echo "BugClerk deployment in directory '${BUGCLERK_HOME}' failed..."
  exit 1
fi

cd ${BUGCLERK_HOME}
./filter-based-run.sh
cd - > /dev/null

rm -rf "${MAVEN_HOME}" "${MVN_ARCHIVE}"
