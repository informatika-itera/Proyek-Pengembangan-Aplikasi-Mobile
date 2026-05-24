package id.pusakakata.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class KbbiResponse(
    val status: Boolean,
    val data: KbbiData? = null,
    val message: String? = null
)

@Serializable
data class KbbiData(
    val lema: String,
    val arti: List<String>
)
