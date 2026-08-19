#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail
source "$( dirname "${BASH_SOURCE[0]}" )/lib/common-functions.sh"

function main {
  wait_for_local_db_to_be_healthy
  select_java_version "21"
  cd "$repo"
  ./mvnw clean install -DskipTests
  cd "$repo"/koodisto-service
  "$repo"/mvnw spring-boot:run -Dspring-boot.run.profiles=dev
}

function wait_for_local_db_to_be_healthy {
  wait_for_container_to_be_healthy oph-koodisto-db
}

function wait_for_container_to_be_healthy {
  require_docker
  local -r container_name="$1"

  info "Waiting for docker container $container_name to be healthy"
  until [ "$(docker inspect -f {{.State.Health.Status}} "$container_name" 2>/dev/null || echo "not-running")" == "healthy" ]; do
    sleep 2
  done
}

main "$@"
