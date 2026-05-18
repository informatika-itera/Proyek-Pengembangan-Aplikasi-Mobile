package com.example.Roomie.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.usecase.GetAllReportsUseCase
import com.example.Roomie.domain.usecase.GetCurrentUserUseCase
import com.example.Roomie.domain.usecase.GetAllAnnouncementsUseCase
import com.example.Roomie.data.repository.ReportRepositoryImpl
import com.example.Roomie.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val userName: String,
        val reportCountInProgress: Int,
        val recentReports: List<Report>,
        val banners: List<String>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val getAllAnnouncementsUseCase: GetAllAnnouncementsUseCase,
    private val reportRepository: ReportRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Seed initial reports if local
        (reportRepository as? ReportRepositoryImpl)?.let {
            viewModelScope.launch {
                it.seedDummyReports()
            }
        }
        observeDashboardData()
    }

    private fun observeDashboardData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            combine(
                getCurrentUserUseCase(),
                getAllReportsUseCase(),
                getAllAnnouncementsUseCase()
            ) { user, reports, announcements ->
                val banners = if (announcements.isEmpty()) {
                    listOf("Selamat datang di Roomie ITERA!")
                } else {
                    announcements.map { it.message }
                }

                HomeUiState.Success(
                    userName = user?.name ?: "User",
                    reportCountInProgress = reports.count { it.status == ReportStatus.IN_PROGRESS },
                    recentReports = reports.takeLast(3).reversed(),
                    banners = banners
                )
            }.collectLatest { successState ->
                _uiState.value = successState
            }
        }
    }
}
