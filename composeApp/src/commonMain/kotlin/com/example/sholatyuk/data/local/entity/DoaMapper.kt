package com.example.sholatyuk.data.local.entity

import com.example.sholatyuk.data.local.DoaEntity
import com.example.sholatyuk.domain.model.Doa
import com.example.sholatyuk.domain.model.DoaCategory

// ── DoaEntity → Domain ────────────────────────────────────────────

fun DoaEntity.toDomain(): Doa = Doa(
    id          = id,
    title       = title,
    arabic      = arabic,
    latin       = latin,
    translation = translation,
    category    = when (category) {
        "DAILY"           -> DoaCategory.DAILY
        "MORNING_EVENING" -> DoaCategory.MORNING_EVENING
        "AFTER_PRAYER"    -> DoaCategory.AFTER_PRAYER
        "SLEEP"           -> DoaCategory.SLEEP
        "EAT"             -> DoaCategory.EAT
        "TRAVEL"          -> DoaCategory.TRAVEL
        "MUSTAJAB"        -> DoaCategory.MUSTAJAB
        else              -> DoaCategory.DAILY
    },
    isFavorite  = is_favorite != 0L
)

fun List<DoaEntity>.toDomainList(): List<Doa> = map { it.toDomain() }

// ── Domain → Insert params ────────────────────────────────────────

fun Doa.categoryKey(): String = category.name

fun Doa.isFavoriteAsLong(): Long = if (isFavorite) 1L else 0L