plugins {
    kotlin("jvm") version "1.9.22"
}

dependencies {
    implementation(project(":system:common"))
    implementation(project(":system:infra"))
    implementation(project(":system:events:events-broker"))
    implementation(project(":system:bounded-context:tasks"))

    implementation(libs.exposed.core)
    implementation(libs.bundles.logs)
    implementation(libs.bundles.http4k)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

