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

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("Halo! Saya asisten kecantikan ROSÉA. Tanyakan apa saja tentang rutinitas skincare atau rekomendasi produk untuk kulitmu. ✨", false))
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        // 1. Tambahkan pesan user ke daftar
        val userMsg = ChatMessage(text = message, isUser = true)
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 2. Ambil data keranjang saat ini secara ringkas
                val cartItems = cartRepository.getCartItems().firstOrNull() ?: emptyList()

                // 3. Gabungkan konteks dengan batasan tegas
                val enrichedMessage = if (cartItems.isNotEmpty()) {
                    val productName = cartItems.first().productName
                    "Konteks tersembunyi (Abaikan jika tidak relevan dengan pertanyaan): Pengguna memiliki '$productName' di keranjang.\n\nPertanyaan pengguna: \"$message\""
                } else {
                    message
                }

                // 4. Kirim pesan ke AI
                val response = aiRepository.chat(enrichedMessage)

                response.fold(
                    onSuccess = { 
                        _messages.value = _messages.value + ChatMessage(text = it, isUser = false)
                    },
                    onFailure = { 
                        _messages.value = _messages.value + ChatMessage(text = "Maaf, terjadi kendala: ${it.message}", isUser = false, isError = true)
                    }
                )
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(text = "Kesalahan sistem: ${e.message}", isUser = false, isError = true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        _messages.value = listOf(ChatMessage("Halo! Saya asisten kecantikan ROSÉA. Tanyakan apa saja tentang rutinitas skincare atau rekomendasi produk untuk kulitmu. ✨", false))
    }
}
