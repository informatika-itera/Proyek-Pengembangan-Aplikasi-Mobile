package com.example.masakuy.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.handleError(
    onError: (Throwable) -> Unit
): Flow<T> = catch { onError(it) }

fun formatCurrency(amount: Int): String {
    return "Rp${String.format("%,d", amount)}".replace(",", ".")
}

fun calculateEstimatedTime(baseTime: Int, complexity: Int): Int {
    return baseTime + (complexity * 5)
}