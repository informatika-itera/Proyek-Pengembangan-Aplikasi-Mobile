package com.example.gamenews.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamenews.data.local.datastore.UserPreferences
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.GameRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class HomeViewModel(
    private val repository: GameRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Semua game dari API (tidak difilter)
    private val _allGames = MutableStateFlow<List<Game>>(emptyList())

    // Genre diambil dari semua game, bukan dari yang terfilter
    val availableGenres: StateFlow<List<String>> = _allGames
        .map { list -> list.map { it.genre }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Game yang ditampilkan = filter dari _allGames
    val games: StateFlow<List<Game>> = combine(_allGames, _searchQuery, _selectedGenre) { all, query, genre ->
        var filtered = all
        if (query.isNotBlank()) {
            filtered = filtered.filter { it.title.contains(query, ignoreCase = true) }
        }
        if (!genre.isNullOrBlank()) {
            filtered = filtered.filter { it.genre.equals(genre, ignoreCase = true) }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            userPreferences.favoriteGenre.first()?.let { savedGenre ->
                _selectedGenre.value = savedGenre
            }
        }
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getLatestGames()
                .catch { _isLoading.value = false }
                .collect { list ->
                    _allGames.value = list
                    _isLoading.value = false
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onGenreSelected(genre: String?) {
        val newGenre = if (_selectedGenre.value == genre) null else genre
        _selectedGenre.value = newGenre
        viewModelScope.launch {
            userPreferences.setFavoriteGenre(newGenre)
        }
    }
}