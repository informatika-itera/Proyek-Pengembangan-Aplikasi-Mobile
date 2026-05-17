package id.pusakakata.domain.model

import kotlinx.datetime.Instant

data class Word(
    val id: String,
    val term: String,
    val definition: String,
    val category: String, // Misal: Sastra, Arkais, Umum
    val srsData: SRSData = SRSData()
) {
    fun copy(
        term: String = this.term,
        definition: String = this.definition,
        category: String = this.category,
        srsData: SRSData = this.srsData
    ) = Word(id, term, definition, category, srsData)
}

data class SRSData(
    val intervalDays: Int = 0,
    val easeFactor: Double = 2.5,
    val nextReview: Instant? = null,
    val level: Int = 0
)
