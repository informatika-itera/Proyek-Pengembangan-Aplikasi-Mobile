package com.example.gamenews.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamenews.domain.repository.GameRepository
import com.example.gamenews.domain.model.Game
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()


    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadGames()
    }

    fun loadGames() {
        viewModelScope.launch {
            _isLoading.value = true

            repository.getLatestGames()
                .catch { e ->

                    _isLoading.value = false
                    _games.value = emptyList()
                }
                .collect { result ->
                    _games.value = result
                    _isLoading.value = false
                }
        }
    }
}