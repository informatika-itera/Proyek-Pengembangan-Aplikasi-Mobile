package com.example.sholatyuk.presentation.screens.home

import com.example.sholatyuk.domain.model.PrayerTime

data class HomeUiState(
    val prayerTime: PrayerTime? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showGpsDialog: Boolean = false
)