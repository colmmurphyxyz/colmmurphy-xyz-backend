val kotlinxCoroutinesVersion = "1.11.0"
val springBootVersion = "4.1.1"
val kotlinVersion = "2.3.21"

plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    kotlin("plugin.lombok") version "2.3.21"
    kotlin("kapt") version "2.3.21"
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    application
}

group = "xyz.colmmurphy"
version = "0.0.1-SNAPSHOT"

application {
    mainClass.set("xyz.colmmurphy.colmmurphyxyzbackend.ColmmurphyxyzBackendApplicationKt")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

configurations.all {
    resolutionStrategy {
        failOnNonReproducibleResolution()
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-rest:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.22.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    compileOnly("org.projectlombok:lombok:1.18.48")
    developmentOnly("org.springframework.boot:spring-boot-devtools:$springBootVersion")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")
    kapt("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")
    annotationProcessor("org.projectlombok:lombok:1.18.48")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinxCoroutinesVersion")

    implementation("com.adamratzman:spotify-api-kotlin-core:4.1.3")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}

kapt {
    keepJavacAnnotationProcessors = true
}