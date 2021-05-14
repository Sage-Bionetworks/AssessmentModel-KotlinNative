plugins {
    id("com.android.library")
    kotlin("android")
//    id("com.github.dcendents.android-maven")
    id ("maven-publish")
    id("org.jetbrains.dokka")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
    }

    compileOptions {
        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
        targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Libs.Jetpack.Compose.version
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isUseProguard = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures.viewBinding = true

}

dependencies {
    api(project(":assessmentModel"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")

    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("org.koin:koin-android:2.2.2")

    implementation ("androidx.compose.compiler:compiler:${Libs.Jetpack.Compose.version}")
    implementation ("androidx.compose.ui:ui:${Libs.Jetpack.Compose.version}")
    // Tooling support (Previews, etc.)
    implementation ("androidx.compose.ui:ui-tooling:${Libs.Jetpack.Compose.version}")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation ("androidx.compose.foundation:foundation:${Libs.Jetpack.Compose.version}")
    // Material Design
    implementation ("androidx.compose.material:material:${Libs.Jetpack.Compose.version}")
    // Material design icons
    implementation ("androidx.compose.material:material-icons-core:${Libs.Jetpack.Compose.version}")
    implementation ("androidx.compose.material:material-icons-extended:${Libs.Jetpack.Compose.version}")
    // Integration with activities
    implementation (Libs.Jetpack.Compose.activity)
    // Integration with ViewModels
    implementation (Libs.Jetpack.Compose.lifecycle)
    // Integration with observables
    implementation ("androidx.compose.runtime:runtime-livedata:${Libs.Jetpack.Compose.version}")
    implementation ("androidx.compose.runtime:runtime-rxjava2:${Libs.Jetpack.Compose.version}")


    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    // UI Tests
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:${Libs.Jetpack.Compose.version}")
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "presentation"
                from(components["release"])
            }
        }
    }

}

//apply("../config/artifact-deploy.gradle")