package id.pusakakata.domain.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

data class Word(
    val id: String,
    val term: String,
    val definition: String,
    val category: String, // Misal: Sastra, Arkais, Umum
    val example: String = "", // Contoh penggunaan kalimat
    val isFavorite: Boolean = false,
    val srsData: SRSData = SRSData()
) {
    fun copy(
        term: String = this.term,
        definition: String = this.definition,
        category: String = this.category,
        example: String = this.example,
        isFavorite: Boolean = this.isFavorite,
        srsData: SRSData = this.srsData
    ) = Word(id, term, definition, category, example, isFavorite, srsData)
}

data class SRSData(
    val intervalDays: Int = 0,
    val easeFactor: Double = 2.5,
    val nextReview: Instant? = null,
    val level: Int = 0
) {
    fun calculateNextReview(quality: Int, currentMoment: Instant): SRSData {
        val newEaseFactor = (easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)))
            .coerceAtLeast(1.3)

        val newInterval = when {
            quality < 3 -> 1
            intervalDays == 0 -> 1
            intervalDays == 1 -> 6
            else -> (intervalDays * newEaseFactor).toInt()
        }

        val nextReview = currentMoment.plus(
            newInterval,
            DateTimeUnit.DAY,
            TimeZone.currentSystemDefault()
        )

        return this.copy(
            intervalDays = newInterval,
            easeFactor = newEaseFactor,
            nextReview = nextReview,
            level = if (quality >= 3) level + 1 else 0
        )
    }
}

