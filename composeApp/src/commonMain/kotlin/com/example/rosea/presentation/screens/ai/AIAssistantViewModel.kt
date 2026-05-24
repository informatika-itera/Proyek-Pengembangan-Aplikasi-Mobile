package com.example.rosea.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosea.domain.repository.AIRepository
import com.example.rosea.domain.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AIUiState>(AIUiState.Initial)
    val uiState: StateFlow<AIUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            _uiState.value = AIUiState.Loading
            try {
                // 1. Ambil data keranjang saat ini secara ringkas
                val cartItems = cartRepository.getCartItems().firstOrNull() ?: emptyList()

                // 2. Susun konteks minimalis untuk menghemat token secara signifikan
                val cartContext = if (cartItems.isNotEmpty()) {
                    "Konteks: Pengguna memiliki [${cartItems.firstOrNull()?.productName}] di keranjang."
                } else ""

                // 3. Gabungkan konteks dengan batasan tegas
                val enrichedMessage = if (cartItems.isNotEmpty()) {
                    val productName = cartItems.first().productName
                    "Konteks tersembunyi (Abaikan jika tidak relevan dengan pertanyaan): Pengguna memiliki '$productName' di keranjang.\n\nPertanyaan pengguna: \"$message\""
                } else {
                    message
                }

                // 4. Kirim pesan hemat token ke AI
                val response = aiRepository.chat(enrichedMessage)

                response.fold(
                    onSuccess = { _uiState.value = AIUiState.Success(it) },
                    onFailure = { _uiState.value = AIUiState.Error(it.message ?: "Terjadi kesalahan") }
                )
            } catch (e: Exception) {
                _uiState.value = AIUiState.Error(e.message ?: "Terjadi kesalahan yang tidak diketahui")
            }
        }
    }

    fun resetState() {
        _uiState.value = AIUiState.Initial
    }
}

sealed interface AIUiState {
    object Initial : AIUiState
    object Loading : AIUiState
    data class Success(val response: String) : AIUiState
    data class Error(val message: String) : AIUiState
}