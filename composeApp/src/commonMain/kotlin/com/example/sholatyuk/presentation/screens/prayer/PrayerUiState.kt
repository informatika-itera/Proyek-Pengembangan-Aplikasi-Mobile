package com.example.sholatyuk.presentation.screens.prayer

import com.example.sholatyuk.domain.model.PrayerTime
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class PrayerUiState(
    val prayerTime: PrayerTime? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
)