package com.example.fitgen.presentation.screens.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.data.local.datastore.UserPreferences
import com.example.fitgen.data.remote.api.ExerciseApiService
import com.example.fitgen.domain.model.Challenge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChallengeDetailUiState(
    val isLoading: Boolean = true,
    val challenge: Challenge? = null,
    val completedDays: Set<Int> = emptySet(),
    val error: String? = null
)

class ChallengeDetailViewModel(
    private val challengeId: String,
    private val apiService: ExerciseApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengeDetailUiState())
    val uiState: StateFlow<ChallengeDetailUiState> = _uiState.asStateFlow()

    init {
        loadChallenge()
    }

    private fun loadChallenge() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = apiService.getChallengeProgram(challengeId)
            
            if (result is com.example.fitgen.core.network.NetworkResult.Success) {
                val challenge = result.data
                
                // Observe completed days from DataStore
                userPreferences.completedChallengeDays.collect { completedSet ->
                    // Parse "challengeId_dayNumber" to get the set of completed days for this specific challenge
                    val completedDaysForThisChallenge = completedSet
                        .filter { it.startsWith("${challengeId}_") }
                        .mapNotNull { it.substringAfter("${challengeId}_").toIntOrNull() }
                        .toSet()
                        
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            challenge = challenge,
                            completedDays = completedDaysForThisChallenge
                        )
                    }
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Gagal memuat challenge."
                    )
                }
            }
        }
    }
}
