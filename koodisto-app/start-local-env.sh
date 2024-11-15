#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail -o xtrace
readonly repo="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

function stop() {
  cd "$repo"
  docker-compose down
}
trap stop EXIT

function main {
  cd "$repo"
  local -r session="koodisto-app"
  tmux kill-session -t "$session" || true
  tmux start-server
  tmux new-session -d -s "$session"

  tmux select-pane -t 0
  tmux send-keys "cd mock-api && npm install && cd - && npm run mock-api" C-m

  tmux splitw -v
  tmux select-pane -t 1
  tmux send-keys "nvm use 20 && npm install && npm run start" C-m

  open "http://localhost:3000/koodisto-app"

  tmux attach-session -t "$session"
}

main "$@"
