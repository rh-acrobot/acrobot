FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:4931ac4d6eab9c0ba3719c1d24d08064cc54bc0f4ba577d445f8a2942d6035ef as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:4931ac4d6eab9c0ba3719c1d24d08064cc54bc0f4ba577d445f8a2942d6035ef
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
