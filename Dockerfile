FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:66175ec6c53f655a24c1c3f81c52b3e281c6918c917435b1014eb75cd11fbe75 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:66175ec6c53f655a24c1c3f81c52b3e281c6918c917435b1014eb75cd11fbe75
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
