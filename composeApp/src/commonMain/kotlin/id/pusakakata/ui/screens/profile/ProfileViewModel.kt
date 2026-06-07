package id.pusakakata.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.*

class ProfileViewModel(private val repository: ItemRepository) : ViewModel() {
    val tokens: StateFlow<Long> = repository.getTokens()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    
    val totalWords: StateFlow<Int> = repository.getAllWords()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    val favoriteCount: StateFlow<Int> = repository.getFavoriteWords()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
