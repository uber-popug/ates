plugins {
    kotlin("jvm") version "1.9.22"
}

dependencies {
    implementation(project(":system:events:events-schema-registry"))

    implementation(libs.bundles.logs)
    implementation(libs.bundles.jackson)
    implementation(libs.kafka)
}
