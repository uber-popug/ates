plugins {
    kotlin("jvm") version "1.9.22"
}

dependencies {
    implementation(project(":system:events:events-schema-registry"))
    implementation(project(":system:events:events-broker"))
    implementation(project(":system:common"))

    implementation(libs.postgresql)
    implementation(libs.bundles.exposed)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
