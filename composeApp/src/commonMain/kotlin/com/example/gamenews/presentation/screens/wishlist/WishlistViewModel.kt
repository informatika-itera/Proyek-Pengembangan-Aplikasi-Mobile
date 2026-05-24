package com.example.gamenews.presentation.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _wishlistGames = MutableStateFlow<List<Game>>(emptyList())
    val wishlistGames: StateFlow<List<Game>> = _wishlistGames.asStateFlow()

    init {
        loadWishlist()
    }

    fun loadWishlist() {
        viewModelScope.launch {
            repository.getWishlistGames()
                .collect { games ->
                    _wishlistGames.value = games
                }
        }
    }

    fun removeFromWishlist(game: Game) {
        viewModelScope.launch {
            repository.toggleWishlist(game)
            loadWishlist() // Refresh list setelah dihapus
        }
    }
}