plugins {
    kotlin("jvm") version "1.9.22"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-core:1.5.1")
    implementation("ch.qos.logback:logback-classic:1.5.1")

    implementation("org.jetbrains.exposed:exposed-core:0.48.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")

    implementation("org.apache.kafka:kafka-clients:3.7.0")

    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:testcontainers:1.19.6")
    testImplementation("org.testcontainers:junit-jupiter:1.19.6")
    testImplementation("org.testcontainers:kafka:1.19.6")
}

tasks.test {
    useJUnitPlatform()
}
