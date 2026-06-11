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
    val picPhone: String = "",
    val imageUrl: String = "",
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
                    item?.let {
                        _uiState.update { state ->
                            state.copy(
                                id = it.id,
                                name = it.name,
                                description = it.description,
                                category = it.category,
                                location = it.location,
                                totalStock = it.totalStock.toString(),
                                availableStock = it.availableStock.toString(),
                                condition = it.condition,
                                picName = it.picName,
                                picPhone = it.picPhone,
                                imageUrl = it.imageUrl ?: ""
                            )
                        }
                    }
                }
            }
        }
    }

    fun onNameChange(name: String) = _uiState.update { it.copy(name = name, error = null) }
    fun onDescriptionChange(desc: String) = _uiState.update { it.copy(description = desc) }
    fun onCategoryChange(cat: ItemCategory) = _uiState.update { it.copy(category = cat) }
    fun onLocationChange(loc: String) = _uiState.update { it.copy(location = loc) }
    fun onTotalStockChange(stock: String) = _uiState.update { it.copy(totalStock = stock) }
    fun onAvailableStockChange(stock: String) = _uiState.update { it.copy(availableStock = stock) }
    fun onConditionChange(cond: ItemCondition) = _uiState.update { it.copy(condition = cond) }
    fun onPicNameChange(pic: String) = _uiState.update { it.copy(picName = pic) }
    fun onPicPhoneChange(phone: String) = _uiState.update { it.copy(picPhone = phone) }
    fun onImageUrlChange(url: String) = _uiState.update { it.copy(imageUrl = url) }

    fun saveItem(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Nama barang tidak boleh kosong") }
            return
        }

        _uiState.update { it.copy(isSaving = true, error = null) }

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
                picName = state.picName,
                picPhone = state.picPhone,
                imageUrl = state.imageUrl.ifBlank { null }
            )

            saveItemUseCase(item)
                .onSuccess { onSuccess() }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isSaving = false, error = e.message ?: "Gagal menyimpan")
                    }
                }

        }
    }
    fun uploadAndSaveImage(imageBytes: ByteArray, fileName: String) {
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            itemRepository.uploadItemImage(imageBytes, fileName)
                .onSuccess { url ->
                    _uiState.update { it.copy(isSaving = false, imageUrl = url) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isSaving = false, error = "Upload foto gagal: ${e.message}")
                    }
                }
        }
    }
}