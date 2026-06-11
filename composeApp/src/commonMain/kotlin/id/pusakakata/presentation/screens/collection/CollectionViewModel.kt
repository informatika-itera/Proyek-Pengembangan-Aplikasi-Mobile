package id.pusakakata.presentation.screens.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.usecase.GachaSystem
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CollectionUiState(
    val allCards: List<LegendaryCard> = emptyList(),
    val collectedIds: Set<String> = emptySet()
)

class CollectionViewModel(
    private val gachaSystem: GachaSystem,
    private val repository: ItemRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CollectionUiState(allCards = gachaSystem.getAllCards()))
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCollectedCardIds().collect { ids ->
                _uiState.update { it.copy(collectedIds = ids.toSet()) }
            }
        }
    }
}
