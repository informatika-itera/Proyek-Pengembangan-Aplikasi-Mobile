package com.example.Roomie.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val userName: String,
        val reportCountInProgress: Int,
        val roomsBookedToday: Int,
        val recentReports: List<Report>,
        val banners: List<String>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Simulasi delay fetch data
                delay(1200)
                
                val recentReports = listOf(
                    Report(
                        id = "1",
                        category = "Gedung Kuliah",
                        location = "GKU 2 - 101",
                        description = "AC Mati",
                        urgency = UrgencyLevel.MEDIUM,
                        status = ReportStatus.IN_PROGRESS,
                        createdAt = 1715673600000
                    ),
                    Report(
                        id = "2",
                        category = "Fasilitas Umum",
                        location = "Kantin",
                        description = "Keran bocor",
                        urgency = UrgencyLevel.LOW,
                        status = ReportStatus.PENDING,
                        createdAt = 1715760000000
                    )
                )

                val banners = listOf(
                    "GKU 1 Lantai 2 sedang perbaikan AC",
                    "Peminjaman Lab Multimedia sudah dibuka!",
                    "Info: Pembersihan area Kantin mulai jam 16:00"
                )

                _uiState.value = HomeUiState.Success(
                    userName = "Mulya Delani",
                    reportCountInProgress = 2,
                    roomsBookedToday = 1,
                    recentReports = recentReports,
                    banners = banners
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
