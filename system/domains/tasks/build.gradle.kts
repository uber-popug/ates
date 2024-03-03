plugins {
    kotlin("jvm") version "1.9.22"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":system:common"))

    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.jetbrains.exposed:exposed-core:0.48.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
