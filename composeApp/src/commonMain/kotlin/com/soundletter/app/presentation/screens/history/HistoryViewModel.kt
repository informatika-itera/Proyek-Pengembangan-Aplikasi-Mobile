package com.soundletter.app.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.presentation.screens.home.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryState(
    val historyMessages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            _state.value = _state.value.copy(
                historyMessages = listOf(
                    Message("101", "Budi", "Me", "Pesan terkirim ke Budi", "After Hours"),
                    Message("102", "Ani", "Me", "Pesan terkirim ke Ani", "Save Your Tears")
                ),
                isLoading = false
            )
        }
    }
}
