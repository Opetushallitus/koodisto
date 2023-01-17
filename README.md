# Koodisto 

Koodisto is an application for managing and distributing common codecs to ease up intercommunication 
between services.


## Technologies

Koodisto is Spring Boot application which uses PostgreSQL database as persistent data storage.

## Developing

### Prerequisites

* Java 11
* Maven

### Running tests

`mvn clean test`

### Running application locally

Application runtime requires a real database which can be set up easily with 
[provided docker setup](dev/database).

Local setup should be run with `dev` profile.

In `koodisto-service` directory, run `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

Datasource settings from [application.properties](koodisto-service/src/main/resources/application.properties) can be overridden with 
VM start up parameters as follows: `-Dspring.datasource.username=foo`
