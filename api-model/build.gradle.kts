plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
    }
}