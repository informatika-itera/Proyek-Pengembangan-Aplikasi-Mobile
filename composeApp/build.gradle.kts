import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    id("com.codingfeline.buildkonfig") version "0.15.1"
}

// 1. Membaca local.properties cukup satu kali di sini
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

kotlin {
    // 2. Menggunakan jvmToolchain sebagai pengganti compilerOptions yang error
    jvmToolchain(17)

    androidTarget {
        // compilerOptions yang error sudah dihapus
    }

    iosX64 {
        binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    iosArm64 {
        binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
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

            // Peekaboo Image Picker
            implementation(libs.peekaboo.image.picker)
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
            implementation("io.coil-kt.coil3:coil-gif:3.0.4")
        }

        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.ext.junit)
                implementation(libs.espresso.core)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTestJUnit4)
            }
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.example.fitgen"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fitgen"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // buildConfigField bawaan Android dihapus agar tidak bentrok dengan BuildKonfig KMP
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

sqldelight {
    databases {
        create("FitGenDatabase") {
            packageName.set("com.example.fitgen.data.local")
        }
    }
}

// 3. Konfigurasi BuildKonfig agar semua API Key bisa dipakai di commonMain
buildkonfig {
    packageName = "com.example.fitgen"

    defaultConfigs {
        // Ambil data dari localProperties yang dideklarasikan di paling atas
        val geminiKey = localProperties.getProperty("GEMINI_API_KEY") ?: "KUNCI_TIDAK_DITEMUKAN"
        val rapidKey = localProperties.getProperty("RAPID_API_KEY") ?: ""
        val groqKey = localProperties.getProperty("GROQ_API_KEY") ?: ""
        val wgerKey = localProperties.getProperty("WGER_API_KEY") ?: ""

        // Gunakan FieldSpec.Type.STRING untuk mendefinisikan tipenya
        buildConfigField(FieldSpec.Type.STRING, "GEMINI_API_KEY", geminiKey)
        buildConfigField(FieldSpec.Type.STRING, "RAPID_API_KEY", rapidKey)
        buildConfigField(FieldSpec.Type.STRING, "GROQ_API_KEY", groqKey)
        buildConfigField(FieldSpec.Type.STRING, "WGER_API_KEY", wgerKey)
    }
}