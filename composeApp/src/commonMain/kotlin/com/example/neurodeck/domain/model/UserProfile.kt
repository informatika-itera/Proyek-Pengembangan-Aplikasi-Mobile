package com.example.neurodeck.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * User profile yang persisted di DataStore (local-only, tidak ada login).
 *
 * Sprint 2 scope:
 *   - Cukup nama, username, bio, avatarUri (URI string nullable)
 *   - memberSince auto-set saat first launch (di-init lazy di Repository)
 *
 * Sprint 3+ extension:
 *   - email, total XP, badges earned, dll bisa ditambahkan tanpa breaking change
 *     (data class field baru dengan default value)
 *
 * @property avatarUri  Local URI ke image file. Bisa file://, content://, atau null.
 *                      Coil 3.x bisa handle semua format ini. Sprint 2: tidak ada
 *                      image picker yet — placeholder/default icon dipakai.
 */
data class UserProfile(
    val name: String = DEFAULT_NAME,
    val username: String = DEFAULT_USERNAME,
    val bio: String = "",
    val avatarUri: String? = null,
    val memberSince: Instant = Clock.System.now(),
) {
    companion object {
        const val DEFAULT_NAME = "Mahasiswa NeuroDeck"
        const val DEFAULT_USERNAME = "@mahasiswa"
        const val MAX_NAME_LENGTH = 50
        const val MAX_USERNAME_LENGTH = 30
        const val MAX_BIO_LENGTH = 200
    }
}

/**
 * Theme mode preference. Pakai enum supaya:
 *   - 3 opsi jelas (Light/Dark/System)
 *   - Persistable as String (di-map by name)
 *   - Exhaustive when di Compose Theme application
 */
enum class ThemeMode {
    Light,
    Dark,
    System;

    companion object {
        val DEFAULT = System

        /** Safe parsing dari String (dari DataStore) — fallback ke default kalau invalid. */
        fun fromName(name: String?): ThemeMode = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}