package id.my.sinanonym.mybawanggacha.data.remote.github.api

import id.my.sinanonym.mybawanggacha.data.remote.github.dto.GitHubReleaseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

class GitHubReleaseService(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://api.github.com"
        private const val OWNER = "sinavarasina"
        private const val REPOSITORY = "Proyek-Pengembangan-Aplikasi-Mobile"
        private const val USER_AGENT = "MyBawangGacha"
    }

    suspend fun getLatestRelease(): GitHubReleaseDto {
        return client.get("$BASE_URL/repos/$OWNER/$REPOSITORY/releases/latest") {
            header(HttpHeaders.Accept, "application/vnd.github+json")
            header(HttpHeaders.UserAgent, USER_AGENT)
        }.body()
    }
}
