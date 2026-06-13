import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

fun localProp(key: String, default: String = ""): String =
    localProperties.getProperty(key) ?: default

val versionCodeProp = (System.getenv("VERSION_CODE")
    ?: localProperties.getProperty("VERSION_CODE", "1")).toInt()
val versionNameProp = System.getenv("VERSION_NAME")
    ?: localProperties.getProperty("VERSION_NAME", "1.0.0")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqldelight)
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
}

android {
    namespace  = "com.example.masakuy"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.masakuy"
        minSdk        = libs.versions.android.minSdk.get().toInt()
        targetSdk     = libs.versions.android.targetSdk.get().toInt()
        versionCode   = versionCodeProp
        versionName   = versionNameProp

        buildConfigField(
            "String", "GEMINI_API_KEY",
            "\"${localProp("GEMINI_API_KEY")}\""
        )
        buildConfigField(
            "String", "BASE_URL",
            "\"${localProp("BASE_URL", "https://generativelanguage.googleapis.com/")}\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = System.getenv("SIGNING_STORE_FILE")
                ?: localProperties.getProperty("SIGNING_STORE_FILE")
            if (keystoreFile != null) {
                storeFile     = file(keystoreFile)
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                    ?: localProp("SIGNING_STORE_PASSWORD")
                keyAlias      = System.getenv("SIGNING_KEY_ALIAS")
                    ?: localProp("SIGNING_KEY_ALIAS")
                keyPassword   = System.getenv("SIGNING_KEY_PASSWORD")
                    ?: localProp("SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            signingConfig     = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String", "GEMINI_API_KEY",
                "\"${localProp("GEMINI_API_KEY")}\""
            )
            buildConfigField(
                "String", "BASE_URL",
                "\"${localProp("BASE_URL", "https://generativelanguage.googleapis.com/")}\""
            )
        }
        debug {
            isDebuggable        = true
            applicationIdSuffix = ".debug"
            versionNameSuffix   = "-debug"
            buildConfigField(
                "String", "GEMINI_API_KEY",
                "\"${localProp("GEMINI_API_KEY_DEBUG").ifEmpty { localProp("GEMINI_API_KEY") }}\""
            )
            buildConfigField(
                "String", "BASE_URL",
                "\"${localProp("BASE_URL_DEBUG", "https://generativelanguage.googleapis.com/")}\""
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    }

    buildFeatures {
        compose     = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/androidMain/kotlin", "src/commonMain/kotlin")
            res.srcDirs("src/androidMain/res")
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
        // Map commonTest (and androidUnitTest, if it exists) into the
        // Android "test" source set so testDebugUnitTest actually picks up
        // and compiles these files. Without this, compileDebugUnitTestKotlin
        // reports NO-SOURCE and Kover shows 0% coverage even though the
        // test files exist on disk.
        getByName("test") {
            java.srcDirs("src/commonTest/kotlin", "src/androidUnitTest/kotlin")
        }
    }
}

sqldelight {
    databases {
        create("MasakuyDatabase") {
            packageName.set("com.example.masakuy.data.local")
            srcDirs.setFrom("src/commonMain/sqldelight")
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Lifecycle ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)

    // SQLDelight
    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")

    // Gemini AI
    implementation(libs.generativeai)

    // Ktor
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // Kotlinx
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    // Coil
    implementation(libs.coil.compose)

    // ─── Testing ───────────────────────────────────────────────────────────
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("app.cash.turbine:turbine:1.1.0")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// ─── Kover — code coverage ─────────────────────────────────────────────────
kover { reports {
    filters {
        excludes {
            classes(
                // Generated / framework code — tidak perlu di-cover
                "*.BuildConfig",
                "*.ComposableSingletons*",
                "*_Factory*",
                "*_HiltComponents*",
                "*.ui.theme.*",
                "*Module*",          // Koin modules
                "*Screen*Kt*",       // Generated Compose top-level
                "*.MainActivity*",
                "*.MasakuyApp*"
            )
        }
    }
}


}
