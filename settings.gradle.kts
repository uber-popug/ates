rootProject.name = "awesome-task-exchange-system"

include(":system:common")
include(":system:events:events-broker")
include(":system:events:events-schema-registry")
include(":system:bounded-context:auth")
include(":system:bounded-context:analytic")
include(":system:bounded-context:billing")
include(":system:bounded-context:tasks")
include(":system:infra")
include(":system:services:tasks-service")

// configure dependencies to all projects
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            library("kafka", "org.apache.kafka:kafka-clients:3.7.0")
            library("postgresql", "org.postgresql:postgresql:42.7.1")
            library("slf4j", "org.slf4j:slf4j-api:2.0.9")
            library("json-schema-validator", "com.networknt:json-schema-validator:1.3.3")

            version("logback", "1.5.1")
            library("logback-core", "ch.qos.logback", "logback-core").versionRef("logback")
            library("logback-classic", "ch.qos.logback", "logback-classic").versionRef("logback")
            bundle("logs", listOf("slf4j", "logback-core", "logback-classic"))

            library("jackson-kotlin", "com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
            library("jackson-jsr", "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")
            library("jackson-yaml", "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.1")
            bundle("jackson", listOf("jackson-kotlin", "jackson-jsr"))
            bundle("json-schema", listOf("json-schema-validator", "jackson-kotlin", "jackson-yaml"))

            version("exposed", "0.48.0")
            library("exposed-core", "org.jetbrains.exposed", "exposed-core").versionRef("exposed")
            library("exposed-jdbc", "org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed")
            library("exposed-java-time", "org.jetbrains.exposed", "exposed-java-time").versionRef("exposed")
            bundle("exposed", listOf("exposed-core", "exposed-jdbc", "exposed-java-time"))

            version("http4k", "5.13.9.0")
            library("http4k-core", "org.http4k", "http4k-core").versionRef("http4k")
            library("http4k-server-undertow", "org.http4k", "http4k-server-undertow").versionRef("http4k")
            library("http4k-client-apache", "org.http4k", "http4k-client-apache").versionRef("http4k")
            library("http4k-format-jackson", "org.http4k", "http4k-format-jackson").versionRef("http4k")
            bundle("http4k", listOf("http4k-core", "http4k-server-undertow", "http4k-client-apache", "http4k-format-jackson"))
        }
    }
}
