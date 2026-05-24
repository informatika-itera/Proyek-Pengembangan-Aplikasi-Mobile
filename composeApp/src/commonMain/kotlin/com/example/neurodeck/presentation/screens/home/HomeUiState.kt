package com.example.neurodeck.presentation.screens.home

import com.example.neurodeck.domain.model.Deck

/**
 * UI State untuk Home Tab.
 *
 * Pattern sealed interface (sama dengan DeckLibraryUiState) — exhaustive
 * when di Compose, compiler memaksa handle semua case.
 *
 * Variants:
 *   - Loading: initial load (dashboard fetch beberapa source parallel)
 *   - Success: semua data ready, render dashboard penuh
 *   - Error:   gagal load (kemungkinan rare — semua data dari local DB)
 *
 * Tidak ada `Empty` state — Home selalu punya konten (greeting, tips, stats),
 * walaupun semua angka = 0. Empty state hanya makes sense untuk list-based
 * screen (DeckLibrary, CardList).
 */
sealed interface HomeUiState {

    data object Loading : HomeUiState

    /**
     * Sukses load semua data dashboard. Semua field non-nullable —
     * default values supaya UI bisa render walaupun user baru saja install.
     *
     * @property greeting       Greeting dinamis by waktu: "Selamat Pagi" /
     *                          "Selamat Siang" / "Selamat Sore" / "Selamat Malam"
     * @property userName       Nama user (sementara hardcoded, akan di-wire ke
     *                          UserPreferencesRepository di P3e).
     * @property dueCardsCount  Jumlah kartu yang due untuk di-review hari ini.
     * @property streakDays     Berapa hari berturut-turut user me-review.
     * @property reviewedToday  Berapa kartu sudah di-review hari ini.
     * @property recentDecks    Top 3 deck yang baru-baru ini di-update.
     *                          Dipakai untuk "Continue Learning" section.
     * @property tipOfTheDay    Tips singkat yang rotate per hari.
     */
    data class Success(
        val greeting: String,
        val userName: String,
        val dueCardsCount: Int,
        val streakDays: Int,
        val reviewedToday: Int,
        val recentDecks: List<Deck>,
        val tipOfTheDay: String,
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}