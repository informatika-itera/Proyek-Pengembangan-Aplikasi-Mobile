package com.example.bridgebit.data.local.entity

import com.example.bridgebit.data.local.TranslationEntity
import com.example.bridgebit.domain.model.Translation

// 1. Memetakan data dari Database (SQLite/SQLDelight) menjadi model Domain (Kotlin)
fun TranslationEntity.toDomain(): Translation {
    return Translation(
        id = id,
        sourceText = source_text,
        translatedText = translated_text,
        sourceLanguage = source_language,
        targetLanguage = target_language,
        isVaulted = is_vaulted == 1L, // Di database berupa angka (1/0), di Kotlin jadi Boolean
        createdAt = created_at,
        updatedAt = updated_at
    )
}

// 2. Class perantara untuk membungkus data sebelum dimasukkan ke Database
data class TranslationEntityValues(
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val isVaulted: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

// 3. Memetakan data dari Domain (Kotlin) menjadi nilai yang siap dimasukkan ke Database
fun Translation.toEntityValues(): TranslationEntityValues {
    return TranslationEntityValues(
        sourceText = sourceText,
        translatedText = translatedText,
        sourceLanguage = sourceLanguage,
        targetLanguage = targetLanguage,
        isVaulted = isVaulted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// 4. Memetakan daftar/List data dari Database menjadi daftar/List model Domain
fun List<TranslationEntity>.toDomainList(): List<Translation> {
    return map { it.toDomain() }
}