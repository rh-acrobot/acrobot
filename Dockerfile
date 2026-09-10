FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:cc8a30e9181b0135e6657ca3b824d7b32e4c7f6a664769ef641d4f7031339564 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:cc8a30e9181b0135e6657ca3b824d7b32e4c7f6a664769ef641d4f7031339564
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
