plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.serialization")
}
android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.2"

    defaultConfig {
        minSdkVersion(19)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    viewBinding {
        isEnabled = true
    }

}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    implementation(project(":assessmentModel"))
    implementation(project(":presentation"))

    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.core:core-ktx:1.2.0")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
