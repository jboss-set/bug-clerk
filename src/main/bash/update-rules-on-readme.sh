#!/bin/bash
readonly RULE_URL_PREFIX='https://github.com/jboss-set/bug-clerk/tree/master/src/main/resources/org/jboss/jbossset/bugclerk'


grep -e 'rule' src/main/resources/org/jboss/jbossset/bugclerk/*.drl | cut -f2 -d: | \
    sed -e 's/"//g' -e 's/rule //' -e 's/_.*$//' | sort -u | \
while
  read rulename
do
  echo "* [${rulename}](${RULE_URL_PREFIX}/${rulename}.drl)"
done
