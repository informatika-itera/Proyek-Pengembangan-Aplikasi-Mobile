package com.example.gamenews.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _allGames = MutableStateFlow<List<Game>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val games: StateFlow<List<Game>> = combine(
        _allGames, _searchQuery, _selectedGenre
    ) { games, query, genre ->
        games.filter { game ->
            val matchesQuery = query.isEmpty() ||
                    game.title.contains(query, ignoreCase = true) ||
                    game.description.contains(query, ignoreCase = true)
            val matchesGenre = genre == null ||
                    game.genre.equals(genre, ignoreCase = true)
            matchesQuery && matchesGenre
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daftar genre unik dari semua game
    val availableGenres: StateFlow<List<String>> = _allGames.map { games ->
        games.map { it.genre }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { loadGames() }

    fun loadGames() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getLatestGames()
                .catch {
                    _isLoading.value = false
                    _allGames.value = emptyList()
                }
                .collect { result ->
                    _allGames.value = result
                    _isLoading.value = false
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onGenreSelected(genre: String?) {
        _selectedGenre.value = if (_selectedGenre.value == genre) null else genre
    }
}