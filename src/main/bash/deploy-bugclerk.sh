#!/bin/bash
readonly TARGET_DIR=${1:-$(mktemp -d)}
readonly PROJECT_DIR=${2:-'.'}
readonly BUGCLERK_VERSION=$(grep -e version ${PROJECT_DIR}/pom.xml | head -3 | tail -1 | sed -e 's;</*version>;;g' -e 's;[\t ]*;;g')

copyFileToTargetDir() {
  local file=${1}

  scp "${file}" "${TARGET_DIR}"
}

declare -A files
files['properties']='./bugclerk.properties'
files['jar']="target/bugclerk-${BUGCLERK_VERSION}.jar"
files['cron_script']='target/classes/hourly-run.sh'
files['run_script']='target/classes/run.sh'

echo -n "Deploying BugClerk into ${TARGET_DIR} ... "
for file in "${!files[@]}"
do
  file_path=${files[$file]}
  if [ ! -e ${file_path} ]; then
    echo "File to deploy '${file_path}' is missing - aborting (partial deployment may have happen)."
    exit 1
  fi

  if [ ${file_path: -3} == ".sh" ]; then
    sed -e "s;\(^readonly BUGCLERK_HOME=\${BUGCLERK_HOME:-.\)[^\']*\(.*$\);\1${TARGET_DIR}\2;" \
        -i "${file_path}"
    chmod +x ${file_path}
  fi
  copyFileToTargetDir "${file_path}"
done
echo 'Done.'
