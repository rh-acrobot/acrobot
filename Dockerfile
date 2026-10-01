FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:c1ace6c4a54563bc2912d753b898fcacb9b1c30010cd153419ddd3aa5bbf3b56 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:c1ace6c4a54563bc2912d753b898fcacb9b1c30010cd153419ddd3aa5bbf3b56
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
