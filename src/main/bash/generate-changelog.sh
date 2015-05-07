#!/bin/bash

readonly TAG=${1}

if [ -z "${TAG}" ]; then
  echo "Missing tag , abort..."
  exit 1
else
  git tags | grep ${TAG} -q
  if [ "${?}" -ne 0 ]; then
    echo "No such tag:${TAG}, abort..."
    exit 2
  fi
fi

readonly GIT_LOG=$(mktemp)

git log --no-color ${TAG}..HEAD > "${GIT_LOG}"

cat "${GIT_LOG}"  | sed -e '/^commit/d' -e '/^Date:/d' -e '/^Author:/d' \
    -e '/^$/d' -e '/maven-release-plugin/d' -e 's/^ */* /'

rm "${GIT_LOG}"
