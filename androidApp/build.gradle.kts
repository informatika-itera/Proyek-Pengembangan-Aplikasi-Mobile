import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "id.my.sinanonym.mybawanggacha"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "id.my.sinanonym.mybawanggacha"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = project.findProperty("app.versionCode")
            ?.toString()
            ?.toIntOrNull()
            ?: System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()
            ?: 1
        versionName = project.findProperty("app.versionName")?.toString()
            ?: System.getenv("APP_VERSION_NAME")
            ?: "1.0.0"

        val geminiApiKey = project.findProperty("GEMINI_API_KEY")?.toString()
            ?: System.getenv("GEMINI_API_KEY")
            ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86_64")
            isUniversalApk = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}


abstract class SyncCommonComposeResourcesToAndroidAssets : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDir: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun sync() {
        val destination = outputDir.get()
            .asFile
            .resolve("composeResources/${packageName.get()}")
        val source = inputDir.get().asFile

        destination.deleteRecursively()
        destination.mkdirs()

        if (source.exists()) {
            source.copyRecursively(
                target = destination,
                overwrite = true
            )
        }
    }
}

androidComponents {
    onVariants { variant ->
        val capitalizedVariantName = variant.name.replaceFirstChar { char ->
            char.uppercaseChar()
        }
        val syncComposeResources = tasks.register<SyncCommonComposeResourcesToAndroidAssets>(
            "sync${capitalizedVariantName}CommonComposeResourcesToAndroidAssets"
        ) {
            inputDir.set(
                project(":composeApp")
                    .layout
                    .projectDirectory
                    .dir("src/commonMain/composeResources")
            )
            packageName.set("id.my.sinanonym.mybawanggacha.generated.resources")
        }

        variant.sources.assets?.addGeneratedSourceDirectory(
            syncComposeResources,
            SyncCommonComposeResourcesToAndroidAssets::outputDir
        )
    }
}

dependencies {
    implementation(project(":composeApp"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
}