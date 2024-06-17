FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:27234b342c21ff87d109e9a5c005e08f0ed553e7e0b8782c7604eed1b6553dee as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:27234b342c21ff87d109e9a5c005e08f0ed553e7e0b8782c7604eed1b6553dee
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
