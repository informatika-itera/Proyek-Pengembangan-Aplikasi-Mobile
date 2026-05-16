package com.soundletter.app.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.presentation.screens.home.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailState(
    val message: Message? = null,
    val isLoading: Boolean = false
)

class DetailViewModel : ViewModel() {
    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    fun loadMessage(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // Simulation
            _state.value = _state.value.copy(
                message = Message(id, "Dzakky", "Anon", "Ini adalah detail pesan anonim yang sangat estetik.", "Starboy"),
                isLoading = false
            )
        }
    }
}
