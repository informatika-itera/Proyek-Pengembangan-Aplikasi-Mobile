package com.kelazzz.app.domain.model

/**
 * Preferensi tema aplikasi.
 *
 * SYSTEM mempertahankan perilaku default: mengikuti tema perangkat.
 */
enum class ThemeMode(val displayName: String) {
    SYSTEM("Sistem"),
    LIGHT("Terang"),
    DARK("Gelap");

    companion object {
        fun fromStoredValue(value: String?): ThemeMode {
            return entries.find { it.name == value } ?: SYSTEM
        }
    }
}
