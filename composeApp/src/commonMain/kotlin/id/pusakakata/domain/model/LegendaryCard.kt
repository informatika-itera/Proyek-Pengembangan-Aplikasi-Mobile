package id.pusakakata.domain.model

data class LegendaryCard(
    val id: String,
    val name: String,
    val description: String,
    val rarity: Rarity,
    val imageUrl: String,
    val origin: String // Asal daerah/mitologi
)
