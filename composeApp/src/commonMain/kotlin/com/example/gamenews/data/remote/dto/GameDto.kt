import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class GameBrainResponse(
    val results: List<GameRemoteEntity>,
    @SerialName("total_results") val totalResults: Int,
    val limit: Int,
    val offset: Int
)

@Serializable
data class GameRemoteEntity(
    val id: Int,
    val name: String,
    val year: Int? = null,
    val genre: String? = null,
    val image: String? = null,
    val rating: RatingDTO? = null,
    @SerialName("short_description") val shortDescription: String? = null,
    val developer: String? = null,
    val screenshots: List<String>? = emptyList(),
    val gameplay: String? = null
)

@Serializable
data class RatingDTO(
    val mean: Double,
    val count: Int
)