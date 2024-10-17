FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:04128015e086b99186222b8f44a6aec0d6bd346c25627e023f29efb4224752e4 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM registry.access.redhat.com/ubi9/openjdk-21@sha256:04128015e086b99186222b8f44a6aec0d6bd346c25627e023f29efb4224752e4
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
