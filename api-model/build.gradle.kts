plugins {
    kotlin("multiplatform")
}

kotlin {

    jvm()
    js {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

    }
}