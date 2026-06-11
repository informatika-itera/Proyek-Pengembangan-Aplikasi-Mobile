package com.example.inventra.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.User
import com.example.inventra.domain.model.UserRole
import com.example.inventra.domain.repository.AuthRepository
import com.example.inventra.domain.repository.BorrowRepository
import com.example.inventra.domain.repository.ItemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(val records: List<BorrowRecord>) : HistoryUiState
    data object Empty : HistoryUiState
}

class HistoryViewModel(
    private val borrowRepository: BorrowRepository,
    private val authRepository: AuthRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = flow {
        emit(authRepository.getCurrentUser())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    val uiState: StateFlow<HistoryUiState> = combine(
        borrowRepository.getAllRecords(),
        currentUser
    ) { records, user ->
        if (user == null) return@combine HistoryUiState.Loading
        
        val filteredRecords = if (user.role == UserRole.ADMIN) {
            records
        } else {
            records.filter { it.borrowerId == user.id }
        }
        
        if (filteredRecords.isEmpty()) HistoryUiState.Empty
        else HistoryUiState.Success(filteredRecords)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState.Loading
    )

    fun approveRequest(recordId: Long) {
        viewModelScope.launch {
            borrowRepository.approveRequest(recordId)
        }
    }

    fun approveReturn(recordId: Long) {
        viewModelScope.launch {
            borrowRepository.approveReturn(recordId)
        }
    }

    fun returnItem(recordId: Long, imageBytes: ByteArray?, fileName: String?) {
        viewModelScope.launch {
            if (imageBytes != null && fileName != null) {
                _isUploading.value = true
                itemRepository.uploadItemImage(imageBytes, "return_$fileName")
                    .onSuccess { url ->
                        borrowRepository.returnItem(recordId, url)
                        _isUploading.value = false
                    }
                    .onFailure {
                        _isUploading.value = false
                        // Error handling could be added here
                    }
            } else {
                borrowRepository.returnItem(recordId, null)
            }
        }
    }
}