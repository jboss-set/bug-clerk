#!/bin/bash

usage() {
  echo "$(basename ${0}) <current-version> <next-version>"
}

checkVersion() {
  local version=${1}

  if [ -z "${version}" ] ; then
    usage
    exit 1
  fi
}

readonly CURRENT_VERSION=${1}
readonly NEXT_VERSION=${2}

checkVersion "${CURRENT_VERSION}"
checkVersion "${NEXT_VERSION}"

find . -name pom.xml | \
while
  read pom_file
do
  sed -e "s/${CURRENT_VERSION}/${NEXT_VERSION}/g" -i "${pom_file}"
done
