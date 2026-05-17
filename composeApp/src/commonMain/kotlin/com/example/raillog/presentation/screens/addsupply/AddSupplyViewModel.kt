package com.example.raillog.presentation.screens.addsupply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raillog.domain.model.PartCategory
import com.example.raillog.domain.model.Priority
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.model.SupplyStatus
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddSupplyViewModel(
    private val repository: SupplyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSupplyUiState())
    val uiState: StateFlow<AddSupplyUiState> = _uiState.asStateFlow()

    fun loadItemForEdit(itemId: Long) {
        viewModelScope.launch {
            try {
                val item = repository.getItemById(itemId).first()
                if (item != null) {
                    _uiState.update {
                        it.copy(
                            editingItemId = item.id,
                            name = item.name,
                            partCode = item.partCode,
                            category = item.category,
                            quantity = item.quantity.toString(),
                            unit = item.unit,
                            supplier = item.supplier,
                            priority = item.priority,
                            notes = item.notes,
                            isEditMode = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(generalError = "Gagal memuat data item") }
            }
        }
    }

    fun onEvent(event: AddSupplyEvent) {
        when (event) {
            is AddSupplyEvent.NameChanged ->
                _uiState.update { it.copy(name = event.name, generalError = null) }
            is AddSupplyEvent.PartCodeChanged ->
                _uiState.update { it.copy(partCode = event.code, partCodeError = null, generalError = null) }
            is AddSupplyEvent.CategoryChanged ->
                _uiState.update { it.copy(category = event.category) }
            is AddSupplyEvent.QuantityChanged ->
                _uiState.update { it.copy(quantity = event.quantity) }
            is AddSupplyEvent.UnitChanged ->
                _uiState.update { it.copy(unit = event.unit) }
            is AddSupplyEvent.SupplierChanged ->
                _uiState.update { it.copy(supplier = event.supplier) }
            is AddSupplyEvent.PriorityChanged ->
                _uiState.update { it.copy(priority = event.priority) }
            is AddSupplyEvent.NotesChanged ->
                _uiState.update { it.copy(notes = event.notes) }
            AddSupplyEvent.SaveItem -> saveItem()
        }
    }

    private fun saveItem() {
        val state = _uiState.value
        var hasError = false

        if (state.name.isBlank()) {
            _uiState.update { it.copy(generalError = "Nama Komponen wajib diisi!") }
            hasError = true
        }

        val partCodeRegex = Regex("^PRT-[A-Z0-9]{4}-\\d{3}\$")
        if (state.partCode.isBlank() || !state.partCode.matches(partCodeRegex)) {
            _uiState.update { it.copy(partCodeError = "Format tidak valid. Gunakan struktur PRT-XXXX-000.") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            try {
                if (state.isEditMode && state.editingItemId != null) {
                    val existing = repository.getItemById(state.editingItemId).first()
                    if (existing != null) {
                        val updated = existing.copy(
                            partCode = state.partCode,
                            name = state.name,
                            category = state.category,
                            quantity = state.quantity.toIntOrNull() ?: 0,
                            unit = state.unit,
                            supplier = state.supplier,
                            priority = state.priority,
                            notes = state.notes
                        )
                        repository.updateItem(updated)
                    }
                } else {
                    val item = SupplyItem(
                        partCode = state.partCode,
                        name = state.name,
                        category = state.category,
                        quantity = state.quantity.toIntOrNull() ?: 0,
                        unit = state.unit,
                        supplier = state.supplier,
                        status = SupplyStatus.PENDING,
                        priority = state.priority,
                        notes = state.notes
                    )
                    repository.insertItem(item)
                }
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(generalError = e.message) }
            }
        }
    }
}

data class AddSupplyUiState(
    val editingItemId: Long? = null,
    val isEditMode: Boolean = false,
    val name: String = "",
    val partCode: String = "",
    val category: PartCategory = PartCategory.BOGIE,
    val quantity: String = "",
    val unit: String = "Pcs",
    val supplier: String = "",
    val priority: Priority = Priority.NORMAL,
    val notes: String = "",
    val partCodeError: String? = null,
    val generalError: String? = null,
    val isSaved: Boolean = false
)

sealed interface AddSupplyEvent {
    data class NameChanged(val name: String) : AddSupplyEvent
    data class PartCodeChanged(val code: String) : AddSupplyEvent
    data class CategoryChanged(val category: PartCategory) : AddSupplyEvent
    data class QuantityChanged(val quantity: String) : AddSupplyEvent
    data class UnitChanged(val unit: String) : AddSupplyEvent
    data class SupplierChanged(val supplier: String) : AddSupplyEvent
    data class PriorityChanged(val priority: Priority) : AddSupplyEvent
    data class NotesChanged(val notes: String) : AddSupplyEvent
    data object SaveItem : AddSupplyEvent
}