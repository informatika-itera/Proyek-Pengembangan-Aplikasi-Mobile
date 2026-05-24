package com.example.neurodeck.core.util

import app.cash.sqldelight.db.SqlDriver

/**
 * expect class untuk membuat SQLDelight SqlDriver per platform.
 *
 * Implementasi:
 * - Android: androidMain/DatabaseDriverFactory.android.kt
 * - iOS    : iosMain/DatabaseDriverFactory.ios.kt
 *
 * TODO Sprint 2: setelah schema NeuroDeck (Deck.sq, Card.sq) ditambahkan,
 * SqlDriver ini akan dipakai untuk inisialisasi NeuroDeckDatabase di Koin.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}