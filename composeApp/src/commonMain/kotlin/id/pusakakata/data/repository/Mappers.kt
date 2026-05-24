package id.pusakakata.data.repository

import id.pusakakata.domain.model.Word
import id.pusakakata.domain.model.SRSData
import id.pusakakata.data.local.WordEntity
import kotlinx.datetime.Instant

fun WordEntity.toDomain() = Word(
    id = id,
    term = term,
    definition = definition,
    category = category,
    example = example,
    srsData = SRSData(
        intervalDays = intervalDays.toInt(),
        easeFactor = easeFactor,
        nextReview = nextReview?.let { Instant.fromEpochMilliseconds(it) },
        level = level.toInt()
    )
)
