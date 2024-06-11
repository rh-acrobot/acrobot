FROM eclipse-temurin:21@sha256:ac1545309de7e27001a80d91df2d42865c0bacaec75e016cb4482255d7691187 as builder
RUN mkdir /opt/app
COPY gradle /opt/app/gradle
COPY gradlew /opt/app/gradlew
COPY build.gradle.kts settings.gradle.kts /opt/app
COPY src /opt/app/src
WORKDIR /opt/app
RUN ./gradlew test
RUN ./gradlew install

FROM eclipse-temurin:21@sha256:ac1545309de7e27001a80d91df2d42865c0bacaec75e016cb4482255d7691187
COPY --from=builder /opt/app/build/install/acrobot-slack /opt/app
WORKDIR /opt/app
CMD ["/opt/app/bin/acrobot-slack"]
