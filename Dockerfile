FROM maven:3.10.0-amazoncorretto-21-al2023@sha256:2d2f6cfef10fa7b8ef897aa98d61ee1d06ac888bba4daa4740e959ff3e3de351 AS build
WORKDIR /build
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

ENV NVM_DIR=/opt/nvm

COPY .nvmrc ./
COPY scripts/lib/nvm.sh ${NVM_DIR}/nvm.sh

RUN source "$NVM_DIR/nvm.sh" --no-use \
  && nvm install

COPY koodisto-app ./koodisto-app
WORKDIR /build/koodisto-app

RUN source "$NVM_DIR/nvm.sh" --no-use \
  && nvm use \
  && npm ci \
  && npm run build

WORKDIR /build
COPY koodisto-service ./koodisto-service
COPY koodisto-api ./koodisto-api
COPY pom.xml .
COPY codebuild-mvn-settings.xml .

RUN mvn clean package -s codebuild-mvn-settings.xml -DskipTests

FROM amazoncorretto:21.0.13@sha256:a02f509af88bd1544d5bb42be83a20d9022dc15f388a0007e2c7d0d87170b9f4
WORKDIR /app

COPY --from=build /build/koodisto-service/target/koodisto-service.jar koodisto-service.jar
COPY --chmod=755 <<"EOF" /app/entrypoint.sh
#!/bin/bash
set -o errexit -o nounset -o pipefail
exec java -XX:MaxRAMPercentage=75 -jar koodisto-service.jar
EOF

ENTRYPOINT ["/app/entrypoint.sh"]
