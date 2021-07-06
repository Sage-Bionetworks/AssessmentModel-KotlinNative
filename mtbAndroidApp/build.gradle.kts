plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "edu.northwestern.mobiletoolbox.sampleapp"
        minSdkVersion(19)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    packagingOptions {
        exclude("META-INF/main.kotlin_module")
        pickFirst("META-INF/kotlinx-serialization-runtime.kotlin_module")
    }
    viewBinding {
        isEnabled = true
    }
    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/main/kotlin")
    }

    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation(project(":presentation"))
    implementation(project(":assessmentModel"))
    implementation(project(":mtbPresentation"))

    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    testImplementation("junit:junit:4.12")
}