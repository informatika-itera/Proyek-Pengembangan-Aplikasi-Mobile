package com.studymate.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.domain.repository.NoteRepository
import com.studymate.domain.repository.MantraRepository
import com.studymate.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val noteRepository: NoteRepository,
    private val profileRepository: UserProfileRepository,
    private val mantraRepository: MantraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    private val _mantra = MutableStateFlow<String?>(null)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            val fallbackMantra = mantraRepository.getRandomMantra()

            combine(
                profileRepository.getProfile(),
                noteRepository.getAllNotes(),
                _mantra
            ) { profile, notes, localMantra ->
                val userName = profile?.displayName ?: "Mahasiswa"
                val streak = profile?.currentStreak ?: 0
                val recentNotes = notes.take(3)
                val mantra = localMantra ?: profile?.dailyMantra ?: fallbackMantra
                
                HomeUiState.Success(
                    userName = userName,
                    currentStreak = streak,
                    recentNotes = recentNotes,
                    dailyMantra = mantra,
                    userProfile = profile
                )
            }.collect {
                _uiState.emit(it)
            }
        }
    }

    fun refreshMantra() {
        viewModelScope.launch {
            val currentMantra = (uiState.value as? HomeUiState.Success)?.dailyMantra
            val newMantra = mantraRepository.getRandomMantra(excludeMantra = currentMantra)
            _mantra.value = newMantra
            profileRepository.updateMantra(newMantra)
        }
    }

    fun retry() {
        loadHomeData()
    }
}
