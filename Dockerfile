FROM maven:3.9.12-amazoncorretto-21-al2023@sha256:cfce7b85a5873d8cddd84c234dc55d898c4d32048b440da3eab309bb67ee0b81 AS build
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

FROM amazoncorretto:21.0.9@sha256:9a236fde34eb07f427a89e7a73f19256af188e3476ed7210101b3ed4daa4de1b
WORKDIR /app

COPY --from=build /build/koodisto-service/target/koodisto-service.jar koodisto-service.jar
COPY --chmod=755 <<"EOF" /app/entrypoint.sh
#!/bin/bash
set -o errexit -o nounset -o pipefail
exec java -jar koodisto-service.jar
EOF

ENTRYPOINT ["/app/entrypoint.sh"]
