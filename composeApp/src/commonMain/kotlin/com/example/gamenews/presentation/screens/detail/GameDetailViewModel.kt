package com.example.gamenews.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.AIRepository
import com.example.gamenews.domain.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val repository: GameRepository,
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _game = MutableStateFlow<Game?>(null)
    val game: StateFlow<Game?> = _game.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _aiDescription = MutableStateFlow<String?>(null)
    val aiDescription: StateFlow<String?> = _aiDescription.asStateFlow()

    private val _isGeneratingDescription = MutableStateFlow(false)
    val isGeneratingDescription: StateFlow<Boolean> = _isGeneratingDescription.asStateFlow()

    private val _isWishlisted = MutableStateFlow(false)
    val isWishlisted: StateFlow<Boolean> = _isWishlisted.asStateFlow()

    fun loadGame(id: Long) {
        viewModelScope.launch {
            checkWishlistStatus(id)
            if (_game.value != null && _game.value?.id?.toLong() == id && !_game.value?.description.isNullOrBlank()) {
                return@launch
            }

            repository.getGameById(id)
                .onStart {
                    if (_game.value == null || _game.value?.id?.toLong() != id) {
                        _isLoading.value = true
                    }
                }
                .catch { _isLoading.value = false }
                .collect { result ->
                    if (result != null) {
                        _game.value = result
                        if (result.description.isNotBlank()) {
                            _isLoading.value = false
                        }
                        if (_aiDescription.value == null) {
                            generateAIDescription(result)
                        }
                    } else {
                        _isLoading.value = false
                    }
                }
        }
    }

    private fun checkWishlistStatus(id: Long) {
        viewModelScope.launch {
            repository.isGameWishlisted(id).collect { status ->
                _isWishlisted.value = status
            }
        }
    }

    fun toggleWishlist() {
        viewModelScope.launch {
            _game.value?.let {
                repository.toggleWishlist(it)
                _isWishlisted.value = !_isWishlisted.value
            }
        }
    }

    private fun generateAIDescription(game: Game) {
        viewModelScope.launch {
            _isGeneratingDescription.value = true
            aiRepository.generateGameDescription(
                title = game.title,
                genre = game.genre,
                developer = game.developer,
                year = game.releaseYear
            ).onSuccess { description ->
                _aiDescription.value = description
            }.onFailure {
                _aiDescription.value = null
            }
            _isGeneratingDescription.value = false
        }
    }
}