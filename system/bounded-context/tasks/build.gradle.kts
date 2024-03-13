plugins {
    kotlin("jvm") version "1.9.22"
}

dependencies {
    implementation(project(":system:events:events-schema-registry"))
    implementation(project(":system:events:events-broker"))
    implementation(project(":system:common"))

    implementation(libs.bundles.exposed)
    implementation(libs.postgresql)

    testImplementation(kotlin("test"))
}
repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
