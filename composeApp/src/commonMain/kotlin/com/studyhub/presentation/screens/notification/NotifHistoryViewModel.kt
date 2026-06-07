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
}

class NotifHistoryViewModel(
    private val getNotifHistoryUseCase: GetNotifHistoryUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase,
    private val markNotifReadUseCase: MarkNotifReadUseCase,
    private val deleteNotifHistoryUseCase: DeleteNotifHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotifHistoryUiState>(NotifHistoryUiState.Loading)
    val uiState: StateFlow<NotifHistoryUiState> = _uiState.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        loadUnreadCount()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = NotifHistoryUiState.Loading
            try {
                val items = getNotifHistoryUseCase()
                val count = getUnreadCountUseCase()
                _uiState.value = if (items.isEmpty())
                    NotifHistoryUiState.Empty
                else
                    NotifHistoryUiState.Success(items, count)
            } catch (e: Exception) {
                _uiState.value = NotifHistoryUiState.Empty
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            markNotifReadUseCase()
            loadHistory()
            loadUnreadCount()
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            deleteNotifHistoryUseCase(id)
            loadHistory()
            loadUnreadCount()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            deleteNotifHistoryUseCase()
            loadHistory()
            loadUnreadCount()
        }
    }

    private fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                val count = getUnreadCountUseCase()
                _unreadCount.value = count
            } catch (e: Exception) { }
        }
    }
}
