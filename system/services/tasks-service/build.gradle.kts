plugins {
    kotlin("jvm") version "1.9.22"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":system:common"))
    implementation(project(":system:domains:tasks"))

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-core:1.5.1")
    implementation("ch.qos.logback:logback-classic:1.5.1")

    implementation(platform("org.http4k:http4k-bom:5.13.9.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-client-apache")
    implementation("org.http4k:http4k-format-jackson")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

