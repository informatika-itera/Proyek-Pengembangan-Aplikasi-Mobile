package com.example.sholatyuk.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class KajianNote(
    val id: String,
    val judul: String,
    val ustadz: String,
    val tanggal: String,
    val kategori: KajianCategory,
    val isi: String
)

enum class KajianCategory(val displayName: String, val icon: String) {
    AQIDAH("Aqidah", "📖"),
    FIQIH("Fiqih", "⚖️"),
    TAFSIR("Tafsir", "🔍"),
    HADITS("Hadits", "📜"),
    AKHLAK("Akhlak", "🤝"),
    LAINNYA("Lainnya", "✨")
}