import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.time.Instant

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

compose.resources {
    packageOfResClass = "id.my.sinanonym.mybawanggacha.generated.resources"
}

kotlin {
    androidLibrary {
        namespace = "id.my.sinanonym.mybawanggacha.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    listOf(
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
            // activity.compose dan uiToolingPreview sudah dipindah ke androidApp
            implementation(libs.koin.androidx.compose)
            implementation(libs.ktor.client.android)
            implementation(libs.sqlDelight.android.driver)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqlDelight.native.driver)
        }

        commonMain {
            kotlin.srcDir(layout.buildDirectory.dir("generated/build-info/commonMain/kotlin"))
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            // SQLDelight
            implementation(libs.sqlDelight.runtime)
            implementation(libs.sqlDelight.coroutines)

            // KotlinX
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            // DataStore
            implementation(libs.datastore.preferences.core)

            // Okio
            implementation(libs.okio)

            // Navigation
            implementation(libs.navigation.compose)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(compose.materialIconsExtended)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.ktor.client.mock)
        }

        androidUnitTest.dependencies {
            implementation(libs.kotlin.test.junit)
        }
    }
}


dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

sqldelight {
    databases {
        create("NoteDatabase") {
            packageName.set("id.my.sinanonym.mybawanggacha.data.local")
        }
    }
}

// ==================== BUILD INFO CONFIGURATION ====================

abstract class GenerateBuildInfoTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val repositoryRoot: Property<String>

    @get:Input
    abstract val versionName: Property<String>

    @get:Input
    abstract val versionCode: Property<String>

    @get:Input
    abstract val buildProfile: Property<String>

    @get:Input
    abstract val buildTarget: Property<String>

    @get:Input
    abstract val repository: Property<String>

    @get:Input
    abstract val branchOverride: Property<String>

    @get:Input
    abstract val commitOverride: Property<String>

    @get:Input
    abstract val commitStateOverride: Property<String>

    @get:Input
    abstract val buildDateOverride: Property<String>

    @get:Input
    abstract val ci: Property<String>

    @get:Input
    abstract val runId: Property<String>

    @TaskAction
    fun generate() {
        val generatedDir = outputDir.get().asFile.resolve("id/my/sinanonym/mybawanggacha/core/build")
        generatedDir.mkdirs()

        val branch = branchOverride.get().takeKnown()
            ?: gitOutput("rev-parse", "--abbrev-ref", "HEAD")
        val commit = commitOverride.get().takeKnown()
            ?: gitOutput("rev-parse", "HEAD")
        val commitState = commitStateOverride.get().takeKnown()
            ?: gitCommitState()
        val buildDate = buildDateOverride.get().takeKnown()
            ?: Instant.now().toString()

        val content = """
            package id.my.sinanonym.mybawanggacha.core.build

            internal object GeneratedBuildInfo {
                const val VERSION_NAME = "${kotlinStringLiteral(versionName.get())}"
                const val VERSION_CODE = "${kotlinStringLiteral(versionCode.get())}"
                const val BUILD_PROFILE = "${kotlinStringLiteral(buildProfile.get())}"
                const val BUILD_TARGET = "${kotlinStringLiteral(buildTarget.get())}"
                const val REPOSITORY = "${kotlinStringLiteral(repository.get())}"
                const val BRANCH = "${kotlinStringLiteral(branch)}"
                const val COMMIT = "${kotlinStringLiteral(commit)}"
                const val COMMIT_STATE = "${kotlinStringLiteral(commitState)}"
                const val BUILD_DATE = "${kotlinStringLiteral(buildDate)}"
                const val CI = "${kotlinStringLiteral(ci.get())}"
                const val RUN_ID = "${kotlinStringLiteral(runId.get())}"
            }
        """.trimIndent()

        generatedDir.resolve("GeneratedBuildInfo.kt").writeText(content)
    }

    private fun gitCommitState(): String {
        val status = gitOutput("status", "--porcelain")

        return when {
            status == "not embedded" -> "not embedded"
            status.isBlank() -> "clean"
            else -> "dirty"
        }
    }

    private fun gitOutput(vararg args: String): String {
        return try {
            val process = ProcessBuilder(listOf("git", *args))
                .directory(File(repositoryRoot.get()))
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                output.ifBlank { "not embedded" }
            } else {
                "not embedded"
            }
        } catch (_: Exception) {
            "not embedded"
        }
    }

    private fun String.takeKnown(): String? {
        return takeIf { value ->
            value.isNotBlank() && !value.equals("not embedded", ignoreCase = true)
        }
    }

    private fun kotlinStringLiteral(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
    }
}

val generateBuildInfo = tasks.register<GenerateBuildInfoTask>("generateBuildInfo") {
    outputDir.set(layout.buildDirectory.dir("generated/build-info/commonMain/kotlin"))
    repositoryRoot.set(rootDir.absolutePath)

    versionName.set(
        providers.gradleProperty("app.versionName")
            .orElse(providers.environmentVariable("APP_VERSION_NAME"))
            .orElse("1.0-rc3")
    )
    versionCode.set(
        providers.gradleProperty("app.versionCode")
            .orElse(providers.environmentVariable("APP_VERSION_CODE"))
            .orElse(providers.environmentVariable("GITHUB_RUN_NUMBER"))
            .orElse("not embedded")
    )
    buildProfile.set(
        providers.gradleProperty("buildProfile")
            .orElse(providers.environmentVariable("BUILD_PROFILE"))
            .orElse(if (providers.environmentVariable("CI").orNull == "true") "ci" else "local")
    )
    buildTarget.set(
        providers.gradleProperty("buildTarget")
            .orElse(providers.environmentVariable("BUILD_TARGET"))
            .orElse("Kotlin Multiplatform")
    )
    repository.set(
        providers.gradleProperty("buildRepository")
            .orElse(providers.environmentVariable("GITHUB_REPOSITORY").map { "https://github.com/$it" })
            .orElse("https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile")
    )
    branchOverride.set(
        providers.gradleProperty("buildBranch")
            .orElse(providers.environmentVariable("GITHUB_REF_NAME"))
            .orElse("not embedded")
    )
    commitOverride.set(
        providers.gradleProperty("buildCommit")
            .orElse(providers.environmentVariable("GITHUB_SHA"))
            .orElse("not embedded")
    )
    commitStateOverride.set(
        providers.gradleProperty("buildCommitState")
            .orElse("not embedded")
    )
    buildDateOverride.set(
        providers.gradleProperty("buildDate")
            .orElse(providers.environmentVariable("BUILD_DATE"))
            .orElse("not embedded")
    )
    ci.set(
        providers.gradleProperty("buildCi")
            .orElse(providers.environmentVariable("CI"))
            .orElse("false")
    )
    runId.set(
        providers.gradleProperty("buildRunId")
            .orElse(providers.environmentVariable("GITHUB_RUN_ID"))
            .orElse("not embedded")
    )

    outputs.upToDateWhen { false }
}

tasks.configureEach {
    if (name.startsWith("compile") || name.startsWith("assemble")) {
        dependsOn(generateBuildInfo)
    }
}