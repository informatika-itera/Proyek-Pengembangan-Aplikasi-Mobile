package com.example.inventra.presentation.screens.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.model.ItemCondition
import com.example.inventra.domain.repository.ItemRepository
import com.example.inventra.domain.usecase.SaveItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditUiState(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val category: ItemCategory = ItemCategory.OTHER,
    val location: String = "",
    val totalStock: String = "1",
    val availableStock: String = "1",
    val condition: ItemCondition = ItemCondition.GOOD,
    val picName: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

class AddEditItemViewModel(
    private val itemId: Long?,
    private val itemRepository: ItemRepository,
    private val saveItemUseCase: SaveItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        if (itemId != null) {
            viewModelScope.launch {
                itemRepository.getItemById(itemId).collect { item ->
                    item?.let { existingItem ->
                        _uiState.update { 
                            it.copy(
                                id = existingItem.id,
                                name = existingItem.name,
                                description = existingItem.description,
                                category = existingItem.category,
                                location = existingItem.location,
                                totalStock = existingItem.totalStock.toString(),
                                availableStock = existingItem.availableStock.toString(),
                                condition = existingItem.condition,
                                picName = existingItem.picName
                            )
                        }
                    }
                }
            }
        }
    }

    fun onNameChange(name: String) = _uiState.update { it.copy(name = name) }
    fun onDescriptionChange(desc: String) = _uiState.update { it.copy(description = desc) }
    fun onCategoryChange(cat: ItemCategory) = _uiState.update { it.copy(category = cat) }
    fun onLocationChange(loc: String) = _uiState.update { it.copy(location = loc) }
    fun onTotalStockChange(stock: String) = _uiState.update { it.copy(totalStock = stock) }
    fun onAvailableStockChange(stock: String) = _uiState.update { it.copy(availableStock = stock) }
    fun onConditionChange(cond: ItemCondition) = _uiState.update { it.copy(condition = cond) }
    fun onPicNameChange(pic: String) = _uiState.update { it.copy(picName = pic) }

    fun saveItem(onSuccess: () -> Unit) {
        val state = _uiState.value
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            val item = Item(
                id = state.id,
                name = state.name,
                description = state.description,
                category = state.category,
                location = state.location,
                totalStock = state.totalStock.toIntOrNull() ?: 1,
                availableStock = state.availableStock.toIntOrNull() ?: 1,
                condition = state.condition,
                picName = state.picName
            )
            
            val result = saveItemUseCase(item)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiState.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
