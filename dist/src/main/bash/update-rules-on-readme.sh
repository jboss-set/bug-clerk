#!/bin/bash

readonly RULES_RELATIVE_FOLDER_NAME='core/src/main/resources/org/jboss/jbossset/bugclerk/'
readonly RULE_URL_PREFIX="https://github.com/jboss-set/bug-clerk/tree/master/${RULES_RELATIVE_FOLDER_NAME}"

grep -e 'rule' ${RULES_RELATIVE_FOLDER_NAME}/*.drl | sed -e '/FilterIssueEntries.drl/d' | cut -f2 -d: | \
    sed -e 's/"//g' -e 's/rule //' -e 's/_.*$//' | sort -u | \
while
  read rulename
do
  echo "* [${rulename}](${RULE_URL_PREFIX}/${rulename}.drl)"
done
