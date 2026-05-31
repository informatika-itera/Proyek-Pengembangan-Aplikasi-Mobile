package com.example.bridgebit.data.local.entity

import com.example.bridgebit.data.local.TranslationEntity
import com.example.bridgebit.domain.model.Translation

fun TranslationEntity.toDomain(): Translation {
    return Translation(
        id = id,
        sourceText = source_text,
        translatedText = translated_text,
        sourceLanguage = source_language,
        targetLanguage = target_language,
        category = category, // <-- MAPPING BARU
        isVaulted = is_vaulted == 1L,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

data class TranslationEntityValues(
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val category: String, // <-- MAPPING BARU
    val isVaulted: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

fun Translation.toEntityValues(): TranslationEntityValues {
    return TranslationEntityValues(
        sourceText = sourceText,
        translatedText = translatedText,
        sourceLanguage = sourceLanguage,
        targetLanguage = targetLanguage,
        category = category, // <-- MAPPING BARU
        isVaulted = isVaulted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<TranslationEntity>.toDomainList(): List<Translation> {
    return map { it.toDomain() }
}