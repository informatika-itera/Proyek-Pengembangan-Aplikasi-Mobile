package com.kosthub.app.core.util

fun formatJarakKm(km: Double): String {
    return km.toString().replace(".", ",")
}

fun formatHargaTahunan(value: Long): String {
    val digits = value.toString()
    val grouped = digits.reversed().chunked(3).joinToString(".").reversed()
    return "Rp$grouped"
}
