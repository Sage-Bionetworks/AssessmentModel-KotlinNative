buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Libs.androidGradlePlugin)
        classpath(Libs.Kotlin.gradlePlugin)
        classpath(Libs.Kotlin.serializationPlugin)
        classpath(Libs.androidMavenGradlePlugin)
    }
}

plugins {

    id("org.jetbrains.dokka") version Libs.Dokka.version
}

tasks.dokkaHtmlMultiModule {
    outputDirectory.set(rootDir.resolve("docs"))
}

allprojects {
    group = "org.sagebionetworks.assessmentmodel"
    version = "0.4.1"

    repositories {
        google()
        mavenCentral()
    }
    afterEvaluate {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                // 1.0.0-alpha13 of the Compose Compiler requires Kotlin version 1.4.30, suppress to allow 1.5.0-M1
                freeCompilerArgs += listOf(
                    "-P", "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
                )
            }
        }
    }
}
