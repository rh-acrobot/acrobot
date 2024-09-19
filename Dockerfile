FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:7666303e9b1463b3a42fd43b18367be1a932b4c9aac4684a011332ae1f2840bd as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:7666303e9b1463b3a42fd43b18367be1a932b4c9aac4684a011332ae1f2840bd
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
