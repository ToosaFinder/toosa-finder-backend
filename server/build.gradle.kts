plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.flywaydb.flyway")
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation(project(":api-model"))

    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    implementation("io.konform:konform-jvm:0.2.0")

    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    implementation("org.postgresql:postgresql:42.2.14")
    implementation("org.flywaydb:flyway-core")
    implementation("com.zaxxer:HikariCP:3.4.5")

    implementation("org.slf4j:slf4j-api:1.7.30")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testApi("org.junit.jupiter:junit-jupiter-api:5.5.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.0")
    testImplementation("io.rest-assured:rest-assured:4.2.0")
    testImplementation("io.rest-assured:kotlin-extensions:4.2.0")
    testImplementation("io.rest-assured:json-path:4.2.0")
    testImplementation("io.rest-assured:xml-path:4.2.0")
    testImplementation("io.kotest:kotest-assertions-core:4.1.1")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

//    testImplementation("org.testcontainers:testcontainers:1.14.3")
//    testImplementation("org.testcontainers:junit-jupiter:1.14.3")
//    testImplementation("org.testcontainers:postgresql:1.14.3")
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    test {
        useJUnitPlatform {
            excludeTags("integrationTest")
        }
        description = "Runs unit tests only"
        description = "verification"
    }
    task<Test>("allTest") {
        description = "Runs all tests (including integration tests)"
        group = "verification"
        useJUnitPlatform()
    }
}

configurations {
    springBoot {
        mainClassName = "com.toosafinder.AppKt"
    }
}
