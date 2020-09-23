import org.jetbrains.kotlin.js.backend.ast.JsName
import org.jetbrains.kotlin.js.translate.utils.definePackageAlias

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
        useCommonJs()
        binaries.executable()
    }

//    tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile::class.java) {
//        kotlinOptions.moduleKind = "commonJs2"
//    }

    sourceSets {
        val coroutinesVersion = "1.3.9"

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
//                com.toosafinder.api(project(":com.toosafinder.api-model"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
            }
        }
    }
}