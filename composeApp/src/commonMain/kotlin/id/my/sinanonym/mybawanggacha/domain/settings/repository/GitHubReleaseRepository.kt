package id.my.sinanonym.mybawanggacha.domain.settings.repository

import id.my.sinanonym.mybawanggacha.domain.settings.model.GitHubRelease

interface GitHubReleaseRepository {
    suspend fun getLatestRelease(): Result<GitHubRelease>
}
