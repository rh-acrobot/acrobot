FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:26fea73c6d66b0c8191bd2a9b8bba67c22d650a60fcd365be30ed56d8c631a8d as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:26fea73c6d66b0c8191bd2a9b8bba67c22d650a60fcd365be30ed56d8c631a8d
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
