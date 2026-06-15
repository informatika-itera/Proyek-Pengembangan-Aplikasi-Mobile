package com.studyhub.presentation.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.NotifHistoryItem
import com.studyhub.domain.usecase.notification.DeleteNotifHistoryUseCase
import com.studyhub.domain.usecase.notification.GetNotifHistoryUseCase
import com.studyhub.domain.usecase.notification.GetUnreadCountUseCase
import com.studyhub.domain.usecase.notification.MarkNotifReadUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NotifHistoryUiState {
    object Loading : NotifHistoryUiState
    data class Success(
        val items: List<NotifHistoryItem>,
        val unreadCount: Int
    ) : NotifHistoryUiState
    object Empty : NotifHistoryUiState
    data class Error(val message: String) : NotifHistoryUiState
}

class NotifHistoryViewModel(
    private val getNotifHistoryUseCase: GetNotifHistoryUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase,
    private val markNotifReadUseCase: MarkNotifReadUseCase,
    private val deleteNotifHistoryUseCase: DeleteNotifHistoryUseCase,
    private val notifHistoryRepository: com.studyhub.domain.repository.NotifHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotifHistoryUiState>(NotifHistoryUiState.Loading)
    val uiState: StateFlow<NotifHistoryUiState> = _uiState.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            try {
                val items = getNotifHistoryUseCase()
                val count = getUnreadCountUseCase()
                _uiState.value = if (items.isEmpty())
                    NotifHistoryUiState.Empty
                else
                    NotifHistoryUiState.Success(items, count)
            } catch (e: Exception) {
                _uiState.value = NotifHistoryUiState.Error(e.message ?: "Gagal memuat riwayat")
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            try {
                markNotifReadUseCase()
                loadHistory()
            } catch (e: Exception) { }
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            try {
                deleteNotifHistoryUseCase(id)
                loadHistory()
            } catch (e: Exception) { }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            try {
                deleteNotifHistoryUseCase()
                loadHistory()
            } catch (e: Exception) { }
        }
    }
}
