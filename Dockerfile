FROM maven:3.9.12-amazoncorretto-21-al2023@sha256:c52f42e16c60e59e28662140676f7dcec610e89d407d275f87f4cc08586a8900 AS build
WORKDIR /build

RUN dnf install -y nodejs24 \
  && alternatives --install /usr/bin/node node /usr/bin/node-24 90 \
  && alternatives --install /usr/bin/npm npm /usr/bin/npm-24 90 \
  && alternatives --install /usr/bin/npx npx /usr/bin/npx-24 90

COPY koodisto-app ./koodisto-app
WORKDIR /build/koodisto-app
RUN npm ci && npm run build

WORKDIR /build
COPY koodisto-audit ./koodisto-audit
COPY koodisto-service ./koodisto-service
COPY koodisto-api ./koodisto-api
COPY pom.xml .
COPY settings.xml .

RUN mvn clean package -s settings.xml -DskipTests

FROM amazoncorretto:21.0.9@sha256:c26e10ed8cf8300aacc0952a4869ed64e14d47d2cfdb24235b891233e4c5385c
WORKDIR /app

COPY --from=build /build/koodisto-service/target/koodisto-service.jar koodisto-service.jar
COPY --chmod=755 <<"EOF" /app/entrypoint.sh
#!/bin/bash
set -o errexit -o nounset -o pipefail
exec java -jar koodisto-service.jar
EOF

ENTRYPOINT ["/app/entrypoint.sh"]
