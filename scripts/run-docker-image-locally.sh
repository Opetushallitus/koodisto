#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail
source "$( dirname "${BASH_SOURCE[0]}" )/lib/common-functions.sh"

function main {
  cd "${repo}"
  docker build --tag koodisto .
  open "http://localhost:8080/koodisto-service/actuator/health"
  docker run \
    --rm --interactive \
    --network koodisto_default \
    --publish 8080:8080 \
    --env "cas.service=http://localhost:8080/koodisto-service/ui" \
    --env 'spring.datasource.url=jdbc:postgresql://oph-koodisto-db:5432/koodisto' \
    koodisto
}

main "$@"
