FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:bd995811a45eb587f97a105796f8f43df466a4269411a2a1642f0f9799f885db as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:bd995811a45eb587f97a105796f8f43df466a4269411a2a1642f0f9799f885db
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
