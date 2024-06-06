plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    kotlin("kapt") version "1.9.24"
}

group = "com.redhat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("com.slack.api:bolt-socket-mode:1.39.3")
    implementation("javax.websocket:javax.websocket-api:1.1")
    implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:1.19")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("com.mysql:mysql-connector-j:8.4.0")

    implementation("org.hibernate.orm:hibernate-core:6.5.2.Final")
    kapt("org.hibernate.orm:hibernate-jpamodelgen:6.5.2.Final")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}