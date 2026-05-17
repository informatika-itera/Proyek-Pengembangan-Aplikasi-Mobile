package id.pusakakata.domain.model

enum class Rarity(val weight: Int, val displayName: String) {
    COMMON(70, "Biasa"),
    RARE(20, "Langka"),
    EPIC(8, "Legenda"),
    MYTHIC(2, "Mitos")
}
