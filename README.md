# Koodisto

Koodisto is an application for managing and distributing common codecs to ease up intercommunication
between services.

## Technologies

Koodisto is Spring Boot application which uses PostgreSQL database as persistent data storage.

## Developing

### Prerequisites

* Java 21
* tmux
* docker

### Running tests

Run `oph-koodisto-test-db` (e.g. with the start local env script)

`./mvnw clean test`

### Running application locally

Run `./start-local-env.sh`
