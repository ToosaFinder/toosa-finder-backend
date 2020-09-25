@file:Suppress("LocalVariableName")

plugins {
    kotlin("multiplatform")
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */


    jvm()

    //используем оба юекенда котлин-компилятора
    js(IR) {
        browser()
//        useCommonJs()
        binaries.executable()
    }

//    tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile::class.java) {
//        kotlinOptions.moduleKind = "commonJs2"
//    }

    sourceSets {
        val kotlin_coroutines_version: String by project
        val kotlin_serialization_version: String by project
        val ktor_version: String by project

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
//                com.toosafinder.api(project(":com.toosafinder.api-model"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_version")
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-json:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_serialization_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$kotlin_coroutines_version")
                implementation("io.ktor:ktor-client-js:$ktor_version")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("io.ktor:ktor-client-android:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlin_coroutines_version")
            }
        }
    }
}