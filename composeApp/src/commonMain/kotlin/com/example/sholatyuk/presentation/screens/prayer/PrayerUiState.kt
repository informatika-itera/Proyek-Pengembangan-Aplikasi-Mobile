package com.example.sholatyuk.presentation.screens.prayer

import com.example.sholatyuk.domain.model.PrayerTime

data class PrayerUiState(
    val prayerTime: PrayerTime? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)