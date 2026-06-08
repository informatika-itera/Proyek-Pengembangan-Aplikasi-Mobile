import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)

    // Coverage: Kover untuk Kotlin/KMP, JaCoCo sebagai fallback Android unit test.
    id("org.jetbrains.kotlinx.kover")
    jacoco
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.camerax.core)
            implementation(libs.camerax.camera2)
            implementation(libs.camerax.lifecycle)
            implementation(libs.camerax.view)
            implementation(libs.mlkit.barcode.scanning)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.datastore.preferences)
            implementation(libs.okio)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.mockk.android)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.sqldelight.sqlite.driver)
            }
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.example.nutriscan"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nutriscan"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("debug") {
            // Dibutuhkan agar testDebugUnitTest menghasilkan file .exec untuk JaCoCo.
            enableUnitTestCoverage = true
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

sqldelight {
    databases {
        create("NutriScanDatabase") {
            packageName.set("com.example.nutriscan.data.local")
        }
    }
}

// ── Coverage Configuration ────────────────────────────────────────────────
// Kover digunakan sesuai materi Sprint 4.
// Command utama:
// ./gradlew koverHtmlReport
//
// Command module:
// ./gradlew :composeApp:koverHtmlReport
kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.BuildConfig",
                    "*.BuildConfig.*",
                    "*.Manifest*",
                    "*.R",
                    "*.R.*",
                    "*ComposableSingletons*",
                    "*Preview*",
                    "*Database*",
                    "*Queries*",
                    "*MainActivity*",
                    "*Application*"
                )

                packages(
                    "com.example.nutriscan.presentation.theme",
                    "com.example.nutriscan.presentation.navigation",
                    "com.example.nutriscan.core.di"
                )
            }
        }
    }
}

// Fallback JaCoCo.
// Jalankan:
// ./gradlew :composeApp:jacocoTestReport
//
// Output:
// composeApp/build/reports/jacoco/html/index.html
tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Generate JaCoCo coverage report for Android debug unit tests."

    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoTestReport.xml"))
    }

    val excludes = listOf(
        "**/BuildConfig.*",
        "**/BuildConfig*",
        "**/R.class",
        "**/R$*.class",
        "**/*Database*.*",
        "**/*Queries*.*",
        "**/ComposableSingletons*.*",
        "**/di/**",
        "**/theme/**",
        "**/navigation/**",
        "**/*Activity*.*",
        "**/*Application*.*"
    )

    sourceDirectories.setFrom(
        files(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin"
        )
    )

    classDirectories.setFrom(
        files(
            fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
                exclude(excludes)
            },
            fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes")) {
                exclude(excludes)
            }
        )
    )

    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include(
                "outputs/unit_test_code_coverage/debugUnitTest/*.exec",
                "outputs/unit_test_code_coverage/debugUnitTest/**/*.exec",
                "jacoco/*.exec",
                "jacoco/**/*.exec",
                "**/*.ec"
            )
        }
    )
}

// Supaya `check` ikut menjalankan unit test Android.
tasks.named("check") {
    dependsOn("testDebugUnitTest")
}

dependencies {
    debugImplementation(compose.uiTooling)

    // Compose UI test, butuh emulator/device.
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.5")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.5")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
