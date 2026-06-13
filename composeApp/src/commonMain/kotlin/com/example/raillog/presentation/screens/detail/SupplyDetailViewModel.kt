package com.example.raillog.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.model.SupplyStatus
import com.example.raillog.domain.model.TechnicalDocument
import com.example.raillog.domain.repository.SupplyRepository
import com.example.raillog.domain.repository.TechnicalDocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SupplyDetailViewModel(
    private val repository: SupplyRepository,
    private val technicalDocumentRepository: TechnicalDocumentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SupplyDetailUiState>(SupplyDetailUiState.Loading)
    val uiState: StateFlow<SupplyDetailUiState> = _uiState.asStateFlow()

    private val _documents = MutableStateFlow<List<TechnicalDocument>>(emptyList())
    val documents: StateFlow<List<TechnicalDocument>> = _documents.asStateFlow()

    private var currentId: Long? = null

    fun loadItem(id: Long) {
        currentId = id
        viewModelScope.launch {
            repository.getItemById(id).collect { item ->
                if (item != null) {
                    _uiState.value = SupplyDetailUiState.Success(item)
                } else {
                    _uiState.value = SupplyDetailUiState.Error("Data Suku Cadang tidak ditemukan.")
                }
            }
        }
        viewModelScope.launch {
            technicalDocumentRepository.getDocumentsByItem(id).collect { docs ->
                _documents.value = docs
            }
        }
    }

    fun updateStatus(status: SupplyStatus) {
        val id = currentId ?: return
        viewModelScope.launch {
            repository.updateStatus(id, status)
        }
    }

    fun deleteItem(onDeleted: () -> Unit) {
        val id = currentId ?: return
        viewModelScope.launch {
            repository.deleteItem(id)
            onDeleted()
        }
    }
}

sealed interface SupplyDetailUiState {
    data object Loading : SupplyDetailUiState
    data class Success(val item: SupplyItem) : SupplyDetailUiState
    data class Error(val message: String) : SupplyDetailUiState
}