package id.my.sinanonym.mybawanggacha.core.build

import platform.UIKit.UIDevice

actual object AppBuildInfoProvider {
    actual val current: AppBuildInfo
        get() = AppBuildInfo(
            appName = "MyBawangGacha",
            versionName = GeneratedBuildInfo.VERSION_NAME,
            versionCode = GeneratedBuildInfo.VERSION_CODE,
            buildProfile = GeneratedBuildInfo.BUILD_PROFILE,
            buildTarget = GeneratedBuildInfo.BUILD_TARGET,
            runtimePlatform = "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}",
            device = UIDevice.currentDevice.model,
            applicationId = "com.example.mybawanggacha",
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
