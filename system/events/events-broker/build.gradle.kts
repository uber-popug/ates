plugins {
    kotlin("jvm") version "1.9.22"
}

dependencies {
    implementation(project(":system:events:events-schema-registry"))
    implementation("com.networknt:json-schema-validator:1.3.3")
    implementation(libs.bundles.logs)
    implementation(libs.bundles.jackson)
    implementation(libs.kafka)
}
