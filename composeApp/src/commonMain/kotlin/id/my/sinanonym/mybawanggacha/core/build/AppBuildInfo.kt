package id.my.sinanonym.mybawanggacha.core.build

data class AppBuildInfo(
    val appName: String,
    val versionName: String,
    val versionCode: String,
    val buildProfile: String,
    val buildTarget: String,
    val runtimePlatform: String,
    val device: String,
    val applicationId: String,
    val repository: String,
    val branch: String,
    val commit: String,
    val commitState: String,
    val buildDate: String,
    val ci: String,
    val ciRunId: String,
    val databaseSchema: String,
    val dataSource: String,
    val aiProvider: String
) {
    val shortCommit: String
        get() = commit.takeIf { it.isKnownBuildValue() }
            ?.take(8)
            .orEmpty()

    val hasEmbeddedGitMetadata: Boolean
        get() = branch.isKnownBuildValue() ||
            commit.isKnownBuildValue() ||
            buildDate.isKnownBuildValue()

    val versionAbiString: String
        get() = listOfNotNull(
            shortCommit.takeIf { it.isNotBlank() },
            "db$databaseSchema"
        ).joinToString("_")

    companion object {
        fun String.isKnownBuildValue(): Boolean {
            val normalized = trim()
            return normalized.isNotBlank() &&
                !normalized.equals("unknown", ignoreCase = true) &&
                !normalized.equals("not embedded", ignoreCase = true) &&
                !normalized.startsWith("not embedded", ignoreCase = true)
        }
    }
}

expect object AppBuildInfoProvider {
    val current: AppBuildInfo
}
