import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id( "com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
//    id("com.github.dcendents.android-maven")
    id ("maven-publish")
    id("org.jetbrains.dokka")
}

android {

    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(19)
        multiDexEnabled = true //Required when setting minSdkVersion to 20 or lower
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isUseProguard = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
        targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.1")
    // Specify Kotlin/JVM stdlib dependency.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")

    testImplementation("junit:junit:4.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    androidTestImplementation("junit:junit:4.12")
    androidTestImplementation("org.jetbrains.kotlin:kotlin-test")
    androidTestImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

kotlin {
   android("androidLib") {
       publishLibraryVariants("release", "debug")
   }
   
   val buildForDevice = (project.findProperty("device") as? String) == "true"
   val iosTarget = if(buildForDevice) iosArm64("ios") else iosX64("ios")
    println("property.device=${project.findProperty("device")}")
    println("buildForDevice=$buildForDevice")
   iosTarget.binaries {
      framework {
          baseName = "AssessmentModel"
         // Disable bitcode embedding for the simulator build.
         if (!buildForDevice) {
            embedBitcode("disable")
         }
          // Include DSYM in the release build
          freeCompilerArgs += "-Xg0"
          // Include Generics in the module header.
          freeCompilerArgs += "-Xobjc-generics"
      }
   }

   sourceSets {
      commonMain {
         dependencies {
             api (Libs.KotlinX.serialization)
             implementation(Libs.KotlinX.coroutines)

         }
      }
      commonTest {
         dependencies {
            implementation( "org.jetbrains.kotlin:kotlin-test-common")
             implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
         }
      }
       sourceSets["androidLibMain"].dependencies {
           implementation("androidx.appcompat:appcompat:1.1.0")
       }
       sourceSets["iosMain"].dependencies {
       }

    }

    tasks.register("copyFramework") {
        val buildType = project.findProperty("kotlin.build.type") as? String ?: "DEBUG"
        dependsOn("link${buildType.toLowerCase().capitalize()}FrameworkIos")

        doLast {
            val srcFile = (kotlin.targets["ios"] as KotlinNativeTarget).binaries.getFramework(buildType).outputFile
            var targetDir = project.findProperty("configuration.build.dir") as? String
            if (targetDir == null) {
                targetDir = if (buildForDevice) {
                    srcFile.parent.replace("/ios/", "/iosArm64/")
                } else {
                    srcFile.parent.replace("/ios/", "/iosX64/")
                }
            }
            println("copy from ${srcFile.parent} \nto $targetDir")
            copy {
                from(srcFile.parent)
                into(targetDir)
                include( "AssessmentModel.framework/**")
                include("AssessmentModel.framework.dSYM")
            }
        }
    }
}



//apply("../config/artifact-deploy.gradle")

//TODO: syoung 03/24/2020 Figure out why getting a warning that this was already added.
//tasks.register("iosTest")  {
//    val  device = project.findProperty("iosDevice") as? String ?: "iPhone 8"
//    dependsOn("linkDebugTestIos")
//    group = JavaBasePlugin.VERIFICATION_GROUP
//    description = "Runs tests for target 'ios' on an iOS simulator"
//
//    doLast {
//        val  binary = (kotlin.targets["ios"] as KotlinNativeTarget).binaries.getTest("DEBUG").outputFile
//        exec {
//            commandLine("xcrun", "simctl", "spawn", "--standalone", device, binary.absolutePath)
//        }
//    }
//}
