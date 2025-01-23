export repo="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." && pwd )"

function export_aws_credentials {
  local -r env=$1
  export AWS_PROFILE="oph-koodisto-${env}"

  if ! aws sts get-caller-identity >/dev/null; then
    fatal "AWS credentials are not configured env $env. Aborting."
  fi
}

function aws {
  docker run \
    --env AWS_PROFILE \
    --env AWS_DEFAULT_REGION \
    --env AWS_CONTAINER_CREDENTIALS_RELATIVE_URI \
    --volume "${HOME}/.aws:/root/.aws" \
    --volume "$( pwd ):/aws" \
    --rm \
    --interactive \
    amazon/aws-cli:2.23.4 "$@"
}

function init_nodejs {
  export NVM_DIR="${NVM_DIR:-$HOME/.cache/nvm}"
  set +o errexit
  source "$repo/scripts/lib/nvm.sh"
  nvm use "${node_version}" || nvm install "${node_version}"
  set -o errexit
}

function npm_ci_if_needed {
  require_command shasum

  if [ ! -f "package.json" ]; then
    fatal "package.json is missing"
  elif [ ! -f "package-lock.json" ]; then
    info "package-lock.json is missing"
    npm install
  elif [ ! -f "$( npm root )/package.json.checksum" ]; then
    info "package.json checksum missing"
    npm ci
  elif [ ! -f "$( npm root )/package-lock.json.checksum" ]; then
    info "package-lock.json checksum missing"
    npm ci
  elif ! shasum --check "$( npm root )/package.json.checksum"; then
    info "package.json changed"
    npm install
  elif ! shasum --check "$( npm root )/package-lock.json.checksum"; then
    info "package-lock.json changed"
    npm ci
  else
    info "No changes in package.json or package-lock.json"
  fi

  shasum package-lock.json > "$( npm root )/package-lock.json.checksum"
  shasum package.json > "$( npm root )/package.json.checksum"
}

function is_running_on_codebuild {
  [ -n "${CODEBUILD_BUILD_ID:-}" ]
}

function select_java_version {
  if ! is_running_on_codebuild; then
    info "Switching to Java $1"
    java_version="$1"
    JAVA_HOME="$(/usr/libexec/java_home -v "${java_version}")"
    export JAVA_HOME
  else
    info "Running on CodeBuild; Java version is managed in buildspec"
  fi
  java -version
}

function require_command {
  if ! command -v "$1" >/dev/null; then
    fatal "I require $1 but it's not installed. Aborting."
  fi
}

function require_docker {
  require_command docker
  docker ps >/dev/null 2>&1 || fatal "Running 'docker ps' failed. Is docker daemon running? Aborting."
}

function parse_env_from_script_name {
  local -r file_name="$(basename "$0")"
  if echo "${file_name}" | grep -E -q '.+-([^-]+)\.sh$'; then
    local -r env="$(echo "${file_name}" | sed -E -e 's|.+-([^-]+)\.sh$|\1|g')"
    info "Using env $env"
    echo $env
  else
    fatal "Don't call this script directly"
  fi
}

function info {
  log "INFO" "$1"
}

function fatal {
  log "ERROR" "$1"
  exit 1
}

function log {
  local -r level="$1"
  local -r message="$2"
  local -r timestamp=$(date +"%Y-%m-%d %H:%M:%S")

  echo >&2 -e "${timestamp} ${level} ${message}"
}