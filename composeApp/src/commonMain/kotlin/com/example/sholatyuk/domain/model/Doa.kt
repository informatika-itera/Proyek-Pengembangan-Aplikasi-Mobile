package com.example.sholatyuk.domain.model

data class Doa(
    val id: Long = 0,
    val title: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val category: DoaCategory,
    val isFavorite: Boolean = false
)

enum class DoaCategory(val displayName: String) {
    DAILY("Sehari-hari"),
    MORNING_EVENING("Pagi & Petang"),
    AFTER_PRAYER("Setelah Sholat"),
    SLEEP("Tidur & Bangun"),
    EAT("Makan & Minum"),
    TRAVEL("Perjalanan"),
    MUSTAJAB("Doa Mustajab")
}
