package com.example.fitgen.presentation.screens.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.data.remote.api.ExerciseApiService
import com.example.fitgen.domain.model.ChallengeDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChallengeDayUiState(
    val isLoading: Boolean = true,
    val dayData: ChallengeDay? = null,
    val error: String? = null
)

class ChallengeDayViewModel(
    private val challengeId: String,
    private val day: Int,
    private val apiService: ExerciseApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengeDayUiState())
    val uiState: StateFlow<ChallengeDayUiState> = _uiState.asStateFlow()

    init {
        loadDayData()
    }

    private fun loadDayData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = apiService.getChallengeProgram(challengeId)
            if (result is com.example.fitgen.core.network.NetworkResult.Success) {
                val challenge = result.data
                val dayData = challenge.days.find { it.dayNumber == day }
                
                if (dayData != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            dayData = dayData
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Hari tidak ditemukan."
                        )
                    }
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Gagal memuat data."
                    )
                }
            }
        }
    }
}
