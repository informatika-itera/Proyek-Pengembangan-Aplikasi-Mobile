package com.example.mybawanggacha.domain.settings.model

enum class ThemeMode {
    System,
    Light,
    Dark;

    fun resolve(systemDarkTheme: Boolean): Boolean {
        return when (this) {
            System -> systemDarkTheme
            Light -> false
            Dark -> true
        }
    }
}
