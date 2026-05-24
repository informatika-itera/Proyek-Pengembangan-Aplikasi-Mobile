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

    init {
        viewModelScope.launch {
            userPreferences.favoriteGenre.first()?.let { savedGenre ->
                _selectedGenre.value = savedGenre
            }
        }
    }

    val games: StateFlow<List<Game>> = combine(_searchQuery, _selectedGenre) { query, genre ->
        Pair(query, genre)
    }
        .debounce(300)
        .flatMapLatest { (query, genre) ->
            _isLoading.value = true
            repository.searchGames(query = query, genre = genre)
                .onEach { _isLoading.value = false }
                .catch {
                    _isLoading.value = false
                    emit(emptyList())
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableGenres: StateFlow<List<String>> = games
        .map { list -> list.map { it.genre }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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