package com.soundletter.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Message(val id: String, val to: String, val from: String, val content: String, val songTitle: String? = null)

data class HomeState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            _state.value = _state.value.copy(
                messages = listOf(
                    Message("1", "Dzakky", "Anon", "Semangat tugas besarnya!", "Starboy"),
                    Message("2", "Atalie", "Anon", "Good luck for the presentation!", "Cruel Summer")
                ),
                isLoading = false
            )
        }
    }
}
