FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:668700248b0abc5cf6d06948c32b6670557ffac39dfe4ea93ab889313453ac39 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:668700248b0abc5cf6d06948c32b6670557ffac39dfe4ea93ab889313453ac39
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
