package com.example.metaforge.presentation.screens.counterpick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metaforge.data.local.HeroDataSource
import com.example.metaforge.domain.repository.DraftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CounterPickViewModel(
    private val draftRepository: DraftRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<CounterPickUiState>(CounterPickUiState.Idle)
    val uiState: StateFlow<CounterPickUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val allHeroes = HeroDataSource.allHeroes

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun analyzeCounter(heroId: Int) {
        val hero = HeroDataSource.getById(heroId) ?: return
        viewModelScope.launch {
            _uiState.value = CounterPickUiState.Analyzing
            // Sprint 3: akan diganti dengan Gemini API call
            // Untuk Sprint 2, tampilkan state analyzing saja
            _uiState.value = CounterPickUiState.Analyzing
        }
    }

    fun reset() {
        _uiState.value = CounterPickUiState.Idle
        _searchQuery.value = ""
    }
}