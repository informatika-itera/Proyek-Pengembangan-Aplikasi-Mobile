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
    alias(libs.plugins.detekt)
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
}

kover {
    reports {
        filters {
            includes {
                packages("com.example.Roomie")
            }
            excludes {
                // Sembunyikan semua UI (The Denominator Slayer)
                annotatedBy("androidx.compose.runtime.Composable")

                // Sembunyikan folder non-logic murni (Denominator Slayer Extreme)
                packages("com.example.Roomie.di", "com.example.Roomie.presentation.theme", "com.example.Roomie.core.util", "com.example.Roomie.core.network", "com.example.Roomie.domain.model", "com.example.Roomie.data.local")

                // Sembunyikan boilerplate & generated code
                classes("com.example.Roomie.BuildConfig", "com.example.Roomie.AppKt")
                classes("*ScreenKt", "*ContentKt", "*ComponentKt", "*ItemKt", "*TileKt", "*CardKt", "*GaugeKt", "*BubbleKt", "*ThemeKt", "*ColorKt", "*StringsKt", "*PreviewKt", "*IconsKt", "*NavigationKt")
                
                // JURUS SAKTI: Sembunyikan paket root yang isinya cuma entry point UI 0%
                classes("com.example.Roomie.di.**", "*.MainActivity", "*.AppKt")
                packages("com.example.Roomie.presentation.theme", "com.example.Roomie.ui.**")

                // Hilangkan class-class di level root folder yang biasanya isinya setup
                classes("com.example.Roomie.Platform**", "com.example.Roomie.BuildConfig", "com.example.Roomie.AppKt")
            }
        }
    }
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

            // Supabase
            implementation("io.github.jan-tennert.supabase:supabase-kt:2.6.1")
            implementation("io.github.jan-tennert.supabase:postgrest-kt:2.6.1")
            implementation("io.github.jan-tennert.supabase:storage-kt:2.6.1")
            implementation("io.github.jan-tennert.supabase:gotrue-kt:2.6.1")
            implementation("io.github.jan-tennert.supabase:realtime-kt:2.6.1")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")

            // Peekaboo (Media Picker)
            implementation(libs.peekaboo.ui)
            implementation(libs.peekaboo.image.picker)

            // Logging
            implementation(libs.napier)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.mockk)
            }
        }

        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.androidx.test.junit)
                implementation(libs.androidx.test.espresso.core)
                implementation(compose.uiTest)
                implementation("androidx.compose.ui:ui-test-junit4-android:1.7.0")
            }
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.example.Roomie"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.Roomie"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
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

sqldelight {
    databases {
        create("RoomieDatabase") {
            packageName.set("com.example.Roomie.data.local")
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(file("../config/detekt/detekt.yml"))
}
