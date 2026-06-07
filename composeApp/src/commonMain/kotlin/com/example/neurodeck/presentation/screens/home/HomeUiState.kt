package com.example.neurodeck.presentation.screens.home

import com.example.neurodeck.domain.model.Deck

sealed interface HomeUiState {

    data object Loading : HomeUiState

    /**
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
        val avatarUri: String?,
        val dueCardsCount: Int,
        val streakDays: Int,
        val reviewedToday: Int,
        val recentDecks: List<RecentDeckUi>,
        val tipOfTheDay: String,
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}

/**
 * @property deck     Deck domain model.
 * @property dueCount Jumlah kartu jatuh tempo di deck ini sekarang.
 */
data class RecentDeckUi(
    val deck: Deck,
    val dueCount: Int,
)
