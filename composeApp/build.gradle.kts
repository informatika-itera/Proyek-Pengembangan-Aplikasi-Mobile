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
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
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
            implementation("io.insert-koin:koin-test:4.0.0")
        }
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation("androidx.work:work-runtime-ktx:2.9.0")
        }

        androidUnitTest.dependencies {
            implementation("org.robolectric:robolectric:4.12.2")
            implementation("androidx.test.ext:junit:1.1.5")
            implementation("androidx.compose.ui:ui-test-junit4:1.7.0")
            implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.example.todomaster"
    compileSdk = 35

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    
    defaultConfig {
        applicationId = "com.example.todomaster"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        
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

sqldelight {
    databases {
        create("TaskDatabase") {
            packageName.set("com.example.todomaster.data.local")
        }
    }
}

dependencies {
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.0")
    releaseImplementation("androidx.compose.ui:ui-test-manifest:1.7.0")
    "androidTestImplementation"("androidx.test.ext:junit:1.1.5")
    "androidTestImplementation"("androidx.compose.ui:ui-test-junit4:1.7.0")
    "debugImplementation"("androidx.compose.ui:ui-test-manifest:1.7.0")
}