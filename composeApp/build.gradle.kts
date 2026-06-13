import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kover)
}

// Load local.properties for API keys
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
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
        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Kotlin
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)

            // Koin DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

            // DataStore + Okio
            implementation(libs.datastore.preferences)
            implementation(libs.okio)

            // Lifecycle & ViewModel
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.runtime.compose)

            // Navigation
            implementation(libs.navigation.compose)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.work.runtime)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }

        // Repository unit tests (JVM) — pakai SQLDelight JDBC driver in-memory.
        // Source set ini meng-extend commonTest, jadi kotlin-test/coroutines-test/turbine
        // sudah tersedia otomatis tanpa perlu di-declare ulang.
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
            }
        }

        // UI tests (instrumented) — Compose UI testing dijalankan di emulator/device.
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.compose.ui.test.junit4)
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.androidx.test.runner)
            }
        }
    }
}

android {
    namespace = "com.example.neurodeck"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.neurodeck"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // Runner untuk instrumented UI tests (androidInstrumentedTest)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject API key from local.properties
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\""
        )
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
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

    // Supaya unit test (Robolectric/androidUnitTest) bisa baca resource Android
    // dan menjalankan kode yang butuh AndroidManifest minimal.
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

// Manifest helper untuk Compose UI test (createComposeRule butuh ini di debug).
dependencies {
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// ── Kover: konfigurasi laporan coverage ──────────────────────────────────────
// Kita exclude kode yang TIDAK relevan untuk dinilai (auto-generated SQLDelight,
// semua @Composable/UI murni, dan modul DI). Dengan begitu persentase coverage
// mencerminkan kode yang memang kita tulis & uji: domain, ViewModel, mapper,
// repository, dan util. ViewModel ada di package presentation.screens.* sehingga
// package itu TIDAK di-exclude (hanya fungsi @Composable yang difilter via anotasi).
kover {
    reports {
        filters {
            excludes {
                classes(
                    "com.example.neurodeck.data.local.NeuroDeckDatabase*",
                    "com.example.neurodeck.data.local.*Queries",
                    "com.example.neurodeck.data.local.*Entity",
                    "com.example.neurodeck.data.local.SelectAllWithCardCount",
                    "*ComposableSingletons*",
                    "com.example.neurodeck.core.di.*"
                )
                annotatedBy("androidx.compose.runtime.Composable")
            }
        }
    }
}

sqldelight {
    databases {
        create("NeuroDeckDatabase") {
            packageName.set("com.example.neurodeck.data.local")
        }
    }
}