package com.example.sholatyuk.presentation.screens.prayer

import com.example.sholatyuk.domain.model.NextPrayer
import com.example.sholatyuk.domain.model.PrayerTime

data class PrayerUiState(
    val isLoading: Boolean = false,
    val prayerTime: PrayerTime? = null,
    val nextPrayer: NextPrayer? = null,
    val error: String? = null
)