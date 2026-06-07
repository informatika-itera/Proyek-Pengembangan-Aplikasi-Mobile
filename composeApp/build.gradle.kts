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
    alias(libs.plugins.kotlinxKover)
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
    
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }
    
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
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        androidUnitTest.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.test.ext.junit)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTestJUnit4)
        }
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)

            // CameraX & ML Kit for QR Scanner
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.mlkit.barcode.scanning)

            // Keep transitive native libraries compatible with Android 15+ 16 KB pages.
            implementation(libs.androidx.graphics.path)
        }
        
//        iosMain.dependencies {
//            implementation(libs.ktor.client.darwin)
//            implementation(libs.sqldelight.native.driver)
//        }
    }
}

android {
    namespace = "com.kelazzz.app"
    compileSdk = 35
    
    defaultConfig {
        applicationId = "com.kelazzz.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        
        // Inject API key from local.properties
        buildConfigField(
            "String",
            "OPENCODE_API_KEY",
            "\"${localProperties.getProperty("OPENCODE_API_KEY", "")}\""
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
        create("KelazZzDatabase") {
            packageName.set("com.kelazzz.app.data.local")
        }
    }
}

kover {
    reports {
        filters {
            excludes {
                packages(
                    "com.kelazzz.app.core",
                    "com.kelazzz.app.core.*",
                    "com.kelazzz.app.data.local",
                    "com.kelazzz.app.data.local.*",
                    "com.kelazzz.app.data.remote",
                    "com.kelazzz.app.data.remote.*",
                    "com.kelazzz.app.di",
                    "com.kelazzz.app.domain.repository",
                    "com.kelazzz.app.presentation.components",
                    "com.kelazzz.app.presentation.navigation",
                    "com.kelazzz.app.presentation.theme",
                    "com.kelazzz.app.presentation.screens.profile",
                    "com.kelazzz.app.presentation.screens.rekap",
                    "com.kelazzz.app.presentation.screens.jadwal.detail"
                )
                classes(
                    "com.kelazzz.app.AppKt",
                    "*.BuildConfig",
                    "*.R",
                    "*.R$*",
                    "*.ComposableSingletons*",
                    "*.MainActivity",
                    "*.KelazZzApplication",
                    "kelazzz.composeapp.generated.resources.*",
                    "com.kelazzz.app.data.repository.AIRepositoryImpl",
                    "com.kelazzz.app.data.repository.AuthRepositoryImpl",
                    "com.kelazzz.app.data.repository.PresensiRepositoryImpl",
                    "com.kelazzz.app.data.repository.PresensiRepositoryImplKt",
                    "com.kelazzz.app.presentation.screens.ai.AIScreenKt*",
                    "com.kelazzz.app.presentation.screens.home.HomeScreenKt*",
                    "com.kelazzz.app.presentation.screens.jadwal.JadwalListScreenKt*",
                    "com.kelazzz.app.presentation.screens.jadwal.addedit.JadwalAddEditScreenKt*",
                    "com.kelazzz.app.presentation.screens.kalender.KalenderScreenKt*",
                    "com.kelazzz.app.presentation.screens.login.LoginScreenKt*",
                    "com.kelazzz.app.presentation.screens.presensi.PresensiScreenKt*"
                )
            }
        }
        verify {
            rule("Sprint 4 minimum coverage") {
                bound {
                    minValue = 50
                }
            }
        }
    }
}
