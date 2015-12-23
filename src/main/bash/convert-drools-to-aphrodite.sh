#!/bin/bash

readonly PROJET_ROOT=${PROJET_ROOT:-'.'}

declare -A migration_matrix
migration_matrix['org.jboss.pull.shared.connectors.bugzilla.Bug']='org.jboss.set.aphrodite.domain.Issue'
migration_matrix['org.jboss.pull.shared.connectors.bugzilla.Comment']='org.jboss.set.aphrodite.domain.Comment'
migration_matrix['org.jboss.pull.shared.connectors.common.Flag']='org.jboss.set.aphrodite.domain.IssueStatus'

for rulefile in ${PROJET_ROOT}/src/main/resources/org/jboss/jbossset/bugclerk/*.drl
do
  echo "$rulefile:"
  for import in "${!migration_matrix[@]}"
  do
    sed -i "${rulefile}" -e "s/${import}/${migration_matrix[${import}]}/g"
  done
done
