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

    // 🔥 1. PLUGIN KSP
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
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

            // Kotlin & Ktor
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)

            // Koin DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // SQLDelight & DataStore
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.datastore.preferences)
            implementation(libs.okio)

            // Lifecycle & Navigation
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.navigation.compose)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // LIBRARY GEMINI AI
            //implementation("dev.shreyaspatil.generativeai:generativeai-google:0.9.0-1.1.0")
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

            // ==========================================
            // 🔥 LIBRARY KHUSUS ANDROID DI SINI 🔥
            // ==========================================
            implementation("com.google.android.gms:play-services-location:21.2.0")

            // Room Database Runtime & KTX
            val room_version = "2.6.1"
            implementation("androidx.room:room-runtime:$room_version")
            implementation("androidx.room:room-ktx:$room_version")

            implementation("org.osmdroid:osmdroid-android:6.1.18")

            implementation("dev.shreyaspatil.generativeai:generativeai-google:0.9.0-1.1.0")
        }
    }
}

// 🔥 2. KSP COMPILER ROOM
dependencies {
    add("kspAndroid", "androidx.room:room-compiler:2.6.1")
}

android {
    namespace = "com.example.pantaujompo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pantaujompo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // Narik DUA API Key dari local.properties biar aman
        buildConfigField(
            "String",
            "GEMINI_API_KEY_NUTRISI",
            "\"${localProperties.getProperty("GEMINI_API_KEY_NUTRISI", "")}\""
        )
        buildConfigField(
            "String",
            "GEMINI_API_KEY_CHAT",
            "\"${localProperties.getProperty("GEMINI_API_KEY_CHAT", "")}\""
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

sqldelight {
    databases {
        create("NoteDatabase") {
            packageName.set("com.example.pantaujompo.data.local")
        }
    }
}