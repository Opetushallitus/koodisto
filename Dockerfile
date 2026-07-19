FROM maven:3.9.15-amazoncorretto-21-al2023@sha256:e3c1928769e2cd7df6ba55f0afb7c711a6e0a2b7b994e7b2b27316f19d893b0b AS build
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

FROM amazoncorretto:21.0.11@sha256:edb6b3e12b360c67d9bd8ec8b4106ce9285603e0f436e58b74da1fa32da5a378
WORKDIR /app

COPY --from=build /build/koodisto-service/target/koodisto-service.jar koodisto-service.jar
COPY --chmod=755 <<"EOF" /app/entrypoint.sh
#!/bin/bash
set -o errexit -o nounset -o pipefail
exec java -XX:MaxRAMPercentage=75 -jar koodisto-service.jar
EOF

ENTRYPOINT ["/app/entrypoint.sh"]
