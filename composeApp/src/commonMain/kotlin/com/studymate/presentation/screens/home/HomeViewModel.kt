package com.studymate.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.domain.repository.NoteRepository
import com.studymate.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val noteRepository: NoteRepository,
    private val profileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val mantras = listOf(
        "Pendidikan adalah senjata paling ampuh untuk mengubah dunia.",
        "Belajar hari ini, memimpin esok hari.",
        "Kesuksesan bukanlah akhir, kegagalan bukanlah fatal: keberanian untuk melanjutkanlah yang penting.",
        "Akar dari pendidikan memang pahit, namun buahnya sangat manis.",
        "Jangan pernah berhenti belajar, karena hidup tidak pernah berhenti mengajar."
    )

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            combine(
                profileRepository.getProfile(),
                noteRepository.getAllNotes()
            ) { profile, notes ->
                val userName = profile?.name ?: "Mahasiswa"
                val streak = profile?.currentStreak ?: 0
                val recentNotes = notes.take(3)
                
                HomeUiState.Success(
                    userName = userName,
                    currentStreak = streak,
                    recentNotes = recentNotes,
                    dailyMantra = mantras.random()
                )
            }.collect {
                _uiState.emit(it)
            }
        }
    }

    fun retry() {
        loadHomeData()
    }
}
