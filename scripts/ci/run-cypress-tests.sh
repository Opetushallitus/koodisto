#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail
source "$( dirname "${BASH_SOURCE[0]}" )/../lib/common-functions.sh"

function main {
  init_nodejs

  cd "${repo}/koodisto-app/mock-api"
  npm_ci_if_needed
  cd "${repo}/koodisto-app"
  npm_ci_if_needed

  npm run lint
  npm run prettier
  npm run cypress:ci
}

main "$@"