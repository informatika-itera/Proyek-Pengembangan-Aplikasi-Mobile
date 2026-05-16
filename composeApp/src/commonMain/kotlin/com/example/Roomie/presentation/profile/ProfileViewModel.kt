package com.example.Roomie.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.usecase.GetCurrentUserUseCase
import com.example.Roomie.domain.usecase.GetAllReportsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(
        val user: User?,
        val reports: List<Report>,
        val stats: Map<ReportStatus, Int>
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAllReportsUseCase: GetAllReportsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeProfileData()
    }

    private fun observeProfileData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            
            // Observe both user session and reports
            getCurrentUserUseCase().collectLatest { user ->
                getAllReportsUseCase().collectLatest { reports ->
                    val stats = mapOf(
                        ReportStatus.PENDING to reports.count { it.status == ReportStatus.PENDING },
                        ReportStatus.IN_PROGRESS to reports.count { it.status == ReportStatus.IN_PROGRESS },
                        ReportStatus.DONE to reports.count { it.status == ReportStatus.DONE }
                    )
                    _uiState.value = ProfileUiState.Success(
                        user = user,
                        reports = reports.reversed(),
                        stats = stats
                    )
                }
            }
        }
    }
}
