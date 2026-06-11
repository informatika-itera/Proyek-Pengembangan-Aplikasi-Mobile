package id.my.sinanonym.mybawanggacha.data.remote.github.dto

import id.my.sinanonym.mybawanggacha.domain.settings.model.GitHubRelease
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubReleaseDto(
    @SerialName("tag_name")
    val tagName: String = "",
    val name: String = "",
    @SerialName("html_url")
    val htmlUrl: String = ""
)

fun GitHubReleaseDto.toDomain(): GitHubRelease {
    return GitHubRelease(
        tagName = tagName,
        name = name,
        htmlUrl = htmlUrl
    )
}
