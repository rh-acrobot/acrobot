FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:3e281b9da807321a35231b3ba543eeff9b7c736ece3f27a914303d3b8f9da0e1 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:3e281b9da807321a35231b3ba543eeff9b7c736ece3f27a914303d3b8f9da0e1
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
