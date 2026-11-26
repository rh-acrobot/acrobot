FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:dd6e02fb23225cc864dc6acae8a456bcf94432f7583c786c2b6de6fc2b5cab3d as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:dd6e02fb23225cc864dc6acae8a456bcf94432f7583c786c2b6de6fc2b5cab3d
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
