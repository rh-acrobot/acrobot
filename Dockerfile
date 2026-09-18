FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:40929d99200a97ae859994d3a41080befb871c26ae116f7d24bc9aeaa7d31d46 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:40929d99200a97ae859994d3a41080befb871c26ae116f7d24bc9aeaa7d31d46
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
