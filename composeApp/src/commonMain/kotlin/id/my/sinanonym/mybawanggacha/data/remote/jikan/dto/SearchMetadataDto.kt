package id.my.sinanonym.mybawanggacha.data.remote.jikan.dto

import kotlinx.serialization.Serializable

@Serializable
data class JikanFilterOptionResponse(
    val data: List<JikanFilterOptionDto> = emptyList()
)

@Serializable
data class JikanFilterOptionDto(
    val mal_id: Int,
    val name: String? = null,
    val titles: List<JikanFilterOptionTitleDto> = emptyList()
)

@Serializable
data class JikanFilterOptionTitleDto(
    val type: String? = null,
    val title: String
)
