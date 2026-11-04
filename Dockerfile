FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:c8fcb7571677a9f6f1f2f3325c9765103ecc2834ff13ce20eb5ee6d1088f7e12 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:c8fcb7571677a9f6f1f2f3325c9765103ecc2834ff13ce20eb5ee6d1088f7e12
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
