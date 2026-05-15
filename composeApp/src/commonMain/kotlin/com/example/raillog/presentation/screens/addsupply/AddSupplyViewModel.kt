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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddSupplyViewModel(
    private val repository: SupplyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddSupplyUiState())
    val uiState: StateFlow<AddSupplyUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddSupplyEvent) {
        when (event) {
            is AddSupplyEvent.NameChanged -> _uiState.update { it.copy(name = event.name, generalError = null) }
            is AddSupplyEvent.PartCodeChanged -> {
                // Hapus error saat user mulai mengetik lagi
                _uiState.update { it.copy(partCode = event.code, partCodeError = null, generalError = null) }
            }
            is AddSupplyEvent.CategoryChanged -> _uiState.update { it.copy(category = event.category) }
            is AddSupplyEvent.QuantityChanged -> _uiState.update { it.copy(quantity = event.quantity) }
            is AddSupplyEvent.UnitChanged -> _uiState.update { it.copy(unit = event.unit) }
            is AddSupplyEvent.SupplierChanged -> _uiState.update { it.copy(supplier = event.supplier) }
            is AddSupplyEvent.PriorityChanged -> _uiState.update { it.copy(priority = event.priority) }
            is AddSupplyEvent.NotesChanged -> _uiState.update { it.copy(notes = event.notes) }
            AddSupplyEvent.SaveItem -> saveItem()
        }
    }

    private fun saveItem() {
        val state = _uiState.value
        var hasError = false

        // Validasi Nama
        if (state.name.isBlank()) {
            _uiState.update { it.copy(generalError = "Nama Komponen wajib diisi!") }
            hasError = true
        }

        // Validasi Format ID Part (Contoh struktur: PRT-XXXX-000)
        val partCodeRegex = Regex("^PRT-[A-Z0-9]{4}-\\d{3}\$")
        if (state.partCode.isBlank() || !state.partCode.matches(partCodeRegex)) {
            _uiState.update { it.copy(partCodeError = "Format tidak lengkap. Gunakan struktur PRT-XXXX-000.") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            try {
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
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(generalError = e.message) }
            }
        }
    }
}

data class AddSupplyUiState(
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