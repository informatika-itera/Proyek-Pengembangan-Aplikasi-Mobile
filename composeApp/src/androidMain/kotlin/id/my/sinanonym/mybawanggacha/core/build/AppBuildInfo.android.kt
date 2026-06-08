package id.my.sinanonym.mybawanggacha.core.build

import android.os.Build

actual object AppBuildInfoProvider {
    actual val current: AppBuildInfo
        get() = AppBuildInfo(
            appName = "MyBawangGacha",
            versionName = GeneratedBuildInfo.VERSION_NAME,
            versionCode = GeneratedBuildInfo.VERSION_CODE,
            buildProfile = GeneratedBuildInfo.BUILD_PROFILE,
            buildTarget = GeneratedBuildInfo.BUILD_TARGET,
            runtimePlatform = "Android ${Build.VERSION.RELEASE} / API ${Build.VERSION.SDK_INT}",
            device = listOf(Build.MANUFACTURER, Build.MODEL)
                .joinToString(" ")
                .replaceFirstChar { char -> char.uppercase() },
            applicationId = "id.my.sinanonym.mybawanggacha",
            repository = GeneratedBuildInfo.REPOSITORY,
            branch = GeneratedBuildInfo.BRANCH,
            commit = GeneratedBuildInfo.COMMIT,
            commitState = GeneratedBuildInfo.COMMIT_STATE,
            buildDate = GeneratedBuildInfo.BUILD_DATE,
            ci = GeneratedBuildInfo.CI,
            ciRunId = GeneratedBuildInfo.RUN_ID,
            databaseSchema = "7",
            dataSource = "Jikan REST API v4 / unofficial MyAnimeList API",
            aiProvider = "Gemini API"
        )
}
