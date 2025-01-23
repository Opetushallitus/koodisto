#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail
source "$( dirname "${BASH_SOURCE[0]}" )/../lib/common-functions.sh"

trap cleanup EXIT INT QUIT TERM

function main {
  select_java_version 21
  start_database

  cd "${repo}"
  if is_running_on_codebuild; then
    docker compose up -d
    mvn clean install -s ./settings.xml
  else
    mvn clean install
  fi
}

function start_database {
  if is_running_on_codebuild; then
    cd "${repo}"
    docker compose up --detach
  fi
}

function cleanup {
  if is_running_on_codebuild; then
    cd "${repo}"
    docker compose down
  fi
}

main "$@"
