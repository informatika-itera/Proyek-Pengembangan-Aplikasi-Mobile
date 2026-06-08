import org.jetbrains.compose.ExperimentalComposeLibrary
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

        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)

            implementation("androidx.compose.ui:ui-test-junit4:1.7.8")
            implementation("androidx.test.ext:junit:1.2.1")
            implementation("androidx.test:runner:1.6.2")
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.example.fitkos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fitkos"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

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
}

dependencies {
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.8")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    // App entry point / Android generated
                    "com.example.fitkos.MainActivity*",
                    "com.example.fitkos.AppKt*",
                    "com.example.fitkos.BuildConfig*",
                    "com.example.fitkos.Manifest*",
                    "com.example.fitkos.R*",
                    "com.example.fitkos.*R*",
                    "fitkos.composeapp.generated.resources.*",

                    // Core / platform / configuration
                    "com.example.fitkos.core.di.*",
                    "com.example.fitkos.core.network.*",
                    "com.example.fitkos.core.util.*",

                    // Local database / generated / platform-specific data layer
                    "com.example.fitkos.data.local.*",
                    "com.example.fitkos.data.local.composeApp.*",
                    "com.example.fitkos.data.local.datastore.DataStoreFactory*",
                    "com.example.fitkos.data.local.entity.*",

                    // Remote layer / DTO / API
                    "com.example.fitkos.data.remote.*",
                    "com.example.fitkos.data.remote.api.*",
                    "com.example.fitkos.data.remote.dto.*",

                    // Repository interfaces / implementations that are not part of focused coverage scope
                    "com.example.fitkos.domain.repository.*",
                    "com.example.fitkos.data.repository.NoteRepositoryImpl*",
                    "com.example.fitkos.data.repository.WaterRepositoryImpl*",

                    // Pure UI shell / theme / navigation
                    "com.example.fitkos.presentation.components.*",
                    "com.example.fitkos.presentation.navigation.*",
                    "com.example.fitkos.presentation.theme.*",
                    "com.example.fitkos.presentation.screens.splash.*",
                    "com.example.fitkos.presentation.screens.settings.*",
                    "com.example.fitkos.presentation.screens.ai.*",
                    "com.example.fitkos.presentation.screens.detail.*",

                    // Exclude Compose UI screen layout files but keep ViewModel / UiState logic
                    "com.example.fitkos.presentation.screens.addnote.AddNoteScreenKt*",
                    "com.example.fitkos.presentation.screens.dashboard.DashboardScreenKt*",
                    "com.example.fitkos.presentation.screens.exercise.ExerciseScreenKt*",
                    "com.example.fitkos.presentation.screens.home.HomeScreenKt*",
                    "com.example.fitkos.presentation.screens.watertracker.WaterTrackerScreenKt*",

                    "com.example.fitkos.presentation.screens.addnote.*ScreenKt*",
                    "com.example.fitkos.presentation.screens.dashboard.*ScreenKt*",
                    "com.example.fitkos.presentation.screens.exercise.*ScreenKt*",
                    "com.example.fitkos.presentation.screens.home.*ScreenKt*",
                    "com.example.fitkos.presentation.screens.watertracker.*ScreenKt*",

                    "com.example.fitkos.presentation.screens.addnote.*ContentKt*",
                    "com.example.fitkos.presentation.screens.dashboard.*ContentKt*",
                    "com.example.fitkos.presentation.screens.exercise.*ContentKt*",
                    "com.example.fitkos.presentation.screens.home.*ContentKt*",
                    "com.example.fitkos.presentation.screens.watertracker.*ContentKt*",

                    // Compose compiler generated classes
                    "*ComposableSingletons*",
                    "*Preview*",
                    "*Kt$*"
                )
            }
        }
    }
}

sqldelight {
    databases {
        create("NoteDatabase") {
            packageName.set("com.example.fitkos.data.local")
        }
    }
}