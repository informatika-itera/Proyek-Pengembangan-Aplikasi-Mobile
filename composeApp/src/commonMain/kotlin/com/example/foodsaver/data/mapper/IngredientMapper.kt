package com.example.foodsaver.data.mapper

object IngredientMapper {
    private val mapping = mapOf(
        "nasi" to "rice",
        "telur" to "egg",
        "ayam" to "chicken",
        "daging" to "beef",
        "sapi" to "beef",
        "ikan" to "fish",
        "udang" to "shrimp",
        "mie" to "noodles",
        "bakso" to "beef",
        "susu" to "milk",
        "susu uht" to "milk",
        "seledri" to "celery",
        "roti" to "bread",
        "keju" to "cheese",
        "kentang" to "potato",
        "pisang" to "banana",
        "apel" to "apple",
        "tomat" to "tomato",
        "bawang" to "onion",
        "bawang putih" to "garlic",
        "tahu" to "tofu",
        "tempe" to "tempeh",
        "sayur" to "vegetables",
        "wortel" to "carrot",
        "kol" to "cabbage",
        "bayam" to "spinach",
        "tepung" to "flour",
        "minyak" to "oil",
        "gula" to "sugar",
        "garam" to "salt",
        "cabai" to "chili",
        "mentega" to "butter"
    )

    fun mapToEnglish(indonesianName: String): String {
        val lower = indonesianName.lowercase().trim()
        return mapping[lower] ?: lower.replace(" ", "_")
    }
}
