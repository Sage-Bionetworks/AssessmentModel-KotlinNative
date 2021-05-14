object Libs {

    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha09"

    //TODO: migrate to maven-publish-plugin https://developer.android.com/studio/build/maven-publish-plugin
    const val androidMavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:2.1"

    object Kotlin {
        private const val version = "1.5.0-M1"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val serializationPlugin ="org.jetbrains.kotlin:kotlin-serialization:$version"
    }

    object KotlinX {
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
    }

    object Dokka {
        const val version = "1.4.0"
    }

    const val koin = "org.koin:koin-android:3.0.1-beta-1"

    object Jetpack {
        object Compose {
            const val version = "1.0.0-beta02"
            const val activity = "androidx.activity:activity-compose:1.3.0-alpha03";
            const val lifecycle = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha02"

        }
    }
}