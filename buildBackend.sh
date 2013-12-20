#!/bin/bash -eu
# -e: Exit immediately if a command exits with a non-zero status.
# -u: Treat unset variables as an error when substituting.


cd koodisto-api/
mvn clean install -DskipTests=true
cd ..
cd koodisto-service/
mvn clean install -DskipTests=true
cd ..
cd koodisto-esb/
mvn clean install -DskipTests=true
cd ..
cd koodisto-features/
mvn clean install -DskipTests=true
cd ..
