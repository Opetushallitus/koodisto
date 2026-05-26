#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail

repo="$(cd "$(dirname "${BASH_SOURCE[0]}")" && cd ../.. && pwd)"
source "${repo}/scripts/lib/common-functions.sh"

function main {
  run_prettier_check
}

function run_prettier_check {
  init_nodejs

  cd "$repo/infra"
  npm_ci_if_needed
  npx prettier . --check

  cd "$repo/koodisto-app"
  npm_ci_if_needed
  npm run lint
}

main "$@"
