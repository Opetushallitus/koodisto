FROM maven:3.10.2-amazoncorretto-21-al2023@sha256:1ab8d1b69babd2a4fdaaec40bcfc22bfbd417b8b8e6c137017cf53f7999e2ec3 AS build
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

FROM amazoncorretto:21.0.13@sha256:fe9b50d12d33a499fb382d47370017a8dee73a2ab80ea6d5aa9a30ee72c82caa
WORKDIR /app

COPY --from=build /build/koodisto-service/target/koodisto-service.jar koodisto-service.jar
COPY --chmod=755 <<"EOF" /app/entrypoint.sh
#!/bin/bash
set -o errexit -o nounset -o pipefail
exec java -XX:MaxRAMPercentage=75 -jar koodisto-service.jar
EOF

ENTRYPOINT ["/app/entrypoint.sh"]
