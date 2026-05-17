package com.example.sholatyuk.domain.model

data class Dzikir(
    val id: Long = 0,
    val title: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val benefit: String,
    val count: Int,             // Jumlah pengulangan yang dianjurkan
    val currentCount: Int = 0,  // Hitungan tasbih saat ini
    val category: DzikirCategory
)

enum class DzikirCategory(val displayName: String) {
    MORNING("Dzikir Pagi"),
    EVENING("Dzikir Petang"),
    AFTER_PRAYER("Setelah Sholat"),
    ANYTIME("Setiap Saat")
}

data class TasbihSession(
    val id: Long = 0,
    val dzikirId: Long,
    val targetCount: Int,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false
)
