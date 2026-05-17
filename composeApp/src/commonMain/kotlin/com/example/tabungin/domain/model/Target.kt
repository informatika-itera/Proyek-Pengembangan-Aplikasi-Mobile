package com.example.tabungin.domain.model


data class Target(
    val id: Long = 0L,
    val nama: String,
    val targetAmount: Double,
    val terkumpul: Double = 0.0,
    val deadline: String,
    val icon: String = "🎯",
    val warna: String = "#4CAF50",
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    /** 0.0 – 1.0 */
    val progres: Float
        get() = if (targetAmount <= 0.0) 0f
        else (terkumpul / targetAmount).toFloat().coerceIn(0f, 1f)

    val sisaTabungan: Double
        get() = (targetAmount - terkumpul).coerceAtLeast(0.0)

    val tercapai: Boolean
        get() = terkumpul >= targetAmount
}
