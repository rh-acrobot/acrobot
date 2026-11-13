FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:4daa3f2fa0f2f64cc7732dca63ef1ed7c53ff4883cc18571d6f8eca7a8458b46 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:4daa3f2fa0f2f64cc7732dca63ef1ed7c53ff4883cc18571d6f8eca7a8458b46
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
