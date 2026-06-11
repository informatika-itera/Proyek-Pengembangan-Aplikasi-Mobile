package id.my.sinanonym.mybawanggacha.data.repository.settings

import id.my.sinanonym.mybawanggacha.data.remote.github.api.GitHubReleaseService
import id.my.sinanonym.mybawanggacha.data.remote.github.dto.toDomain
import id.my.sinanonym.mybawanggacha.domain.settings.model.GitHubRelease
import id.my.sinanonym.mybawanggacha.domain.settings.repository.GitHubReleaseRepository

class GitHubReleaseRepositoryImpl(
    private val service: GitHubReleaseService
) : GitHubReleaseRepository {
    override suspend fun getLatestRelease(): Result<GitHubRelease> {
        return runCatching {
            service.getLatestRelease().toDomain()
        }
    }
}
