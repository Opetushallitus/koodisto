FROM maven:3.9.16-amazoncorretto-21-al2023@sha256:93fa1cbb35651a3833b0d14bd85fd4511a063e5438e5f723bf40d0683f99f3e4 AS build
WORKDIR /build

RUN dnf install -y nodejs24 \
  && alternatives --install /usr/bin/node node /usr/bin/node-24 90 \
  && alternatives --install /usr/bin/npm npm /usr/bin/npm-24 90 \
  && alternatives --install /usr/bin/npx npx /usr/bin/npx-24 90

COPY koodisto-app ./koodisto-app
WORKDIR /build/koodisto-app
RUN npm ci && npm run build

WORKDIR /build
COPY koodisto-service ./koodisto-service
COPY koodisto-api ./koodisto-api
COPY pom.xml .
COPY codebuild-mvn-settings.xml .

RUN mvn clean package -s codebuild-mvn-settings.xml -DskipTests

FROM amazoncorretto:21.0.12@sha256:540708337af29fda537479f2205d2d219a3780c3f07be5f42be96501789f610b
WORKDIR /app

COPY --from=build /build/koodisto-service/target/koodisto-service.jar koodisto-service.jar
COPY --chmod=755 <<"EOF" /app/entrypoint.sh
#!/bin/bash
set -o errexit -o nounset -o pipefail
exec java -XX:MaxRAMPercentage=75 -jar koodisto-service.jar
EOF

ENTRYPOINT ["/app/entrypoint.sh"]
