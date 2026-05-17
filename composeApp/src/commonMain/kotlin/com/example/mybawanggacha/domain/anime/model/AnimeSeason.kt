package com.example.mybawanggacha.domain.anime.model

enum class AnimeSeason(
    val apiKey: String,
    val defaultLabel: String,
    val order: Int
) {
    Winter("winter", "Musim Dingin", 0),
    Spring("spring", "Musim Semi", 1),
    Summer("summer", "Musim Panas", 2),
    Fall("fall", "Musim Gugur", 3);

    companion object {
        fun fromApiKey(value: String?): AnimeSeason? {
            return entries.firstOrNull { season ->
                season.apiKey == value?.lowercase()
            }
        }

        fun fromMonth(monthNumber: Int): AnimeSeason {
            return when (monthNumber) {
                in 1..3 -> Winter
                in 4..6 -> Spring
                in 7..9 -> Summer
                else -> Fall
            }
        }
    }
}

data class AnimeSeasonPeriod(
    val year: Int,
    val season: AnimeSeason
) {
    val displayLabel: String = "${season.defaultLabel} $year"
    val sortValue: Int = year * 4 + season.order

    fun isBefore(other: AnimeSeasonPeriod): Boolean {
        return sortValue < other.sortValue
    }

    fun previous(): AnimeSeasonPeriod {
        return when (season) {
            AnimeSeason.Winter -> AnimeSeasonPeriod(year - 1, AnimeSeason.Fall)
            AnimeSeason.Spring -> AnimeSeasonPeriod(year, AnimeSeason.Winter)
            AnimeSeason.Summer -> AnimeSeasonPeriod(year, AnimeSeason.Spring)
            AnimeSeason.Fall -> AnimeSeasonPeriod(year, AnimeSeason.Summer)
        }
    }
}
