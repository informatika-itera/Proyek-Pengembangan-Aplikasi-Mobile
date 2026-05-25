package com.example.sholatyuk.data.local.entity

import com.example.sholatyuk.data.local.DzikirEntity
import com.example.sholatyuk.domain.model.Dzikir
import com.example.sholatyuk.domain.model.DzikirCategory

// ── DzikirEntity → Domain ─────────────────────────────────────────

fun DzikirEntity.toDomain(): Dzikir = Dzikir(
    id           = id,
    title        = title,
    arabic       = arabic,
    latin        = latin,
    translation  = translation,
    benefit      = benefit,
    count        = count.toInt(),
    currentCount = current_count.toInt(),
    category     = when (category) {
        "MORNING"      -> DzikirCategory.MORNING
        "EVENING"      -> DzikirCategory.EVENING
        "AFTER_PRAYER" -> DzikirCategory.AFTER_PRAYER
        else           -> DzikirCategory.ANYTIME
    }
)

fun List<DzikirEntity>.toDomainList(): List<Dzikir> = map { it.toDomain() }

// ── Domain → Insert params ────────────────────────────────────────

fun Dzikir.categoryKey(): String = category.name

fun Dzikir.countAsLong(): Long = count.toLong()