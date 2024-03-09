plugins {
    kotlin("jvm") version "1.9.22"
}

dependencies {
    implementation(project(":system:events:events-schema-registry"))
    implementation(project(":system:events:events-broker"))
    implementation(project(":system:common"))

    implementation(libs.bundles.exposed)
    implementation(libs.bundles.jackson)
    implementation(libs.bundles.http4k)
    implementation(libs.postgresql)
    implementation(libs.kafka)
}
repositories {
    mavenCentral()
}
