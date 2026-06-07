package com.example.Roomie.presentation.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.usecase.ai.AIResult
import com.example.Roomie.domain.usecase.ai.ProcessAICommandUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssistantUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("Halo bre! Gua asisten pinter Roomie. Ada yang bisa gua bantu cariin ruangan hari ini?", isUser = false)
    ),
    val isLoading: Boolean = false,
    val suggestedRooms: List<Room> = emptyList(),
    val currentQuery: String = ""
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = 0L // Optional for sorting/display
)

class AssistantViewModel(
    private val processAICommandUseCase: ProcessAICommandUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(currentQuery = query) }
    }

    fun sendCommand() {
        val query = _uiState.value.currentQuery
        if (query.isBlank()) return

        // 1. Add user message to list
        val userMsg = ChatMessage(text = query, isUser = true)
        _uiState.update { 
            it.copy(
                messages = it.messages + userMsg,
                isLoading = true,
                currentQuery = ""
            )
        }

        viewModelScope.launch {
            try {
                // 2. Process with AI UseCase
                val result = processAICommandUseCase(query)
                
                when (result) {
                    is AIResult.Success -> {
                        val aiResponseText = if (result.suggestedRooms.isNotEmpty()) {
                            "Oke bre, gua nemu ${result.suggestedRooms.size} ruangan yang cocok buat lo di tgl ${result.extractedData.date ?: "hari ini"}. Cek di bawah ya!"
                        } else {
                            "Waduh bre, kriteria lo udah gua cari tapi nggak ada ruangan yang pas di jam segitu. Coba ganti jam atau kapasitasnya deh!"
                        }
                        
                        _uiState.update { 
                            it.copy(
                                messages = it.messages + ChatMessage(aiResponseText, isUser = false),
                                suggestedRooms = result.suggestedRooms,
                                isLoading = false
                            )
                        }
                    }
                    is AIResult.Error -> {
                        _uiState.update { 
                            it.copy(
                                messages = it.messages + ChatMessage(result.message, isUser = false),
                                suggestedRooms = emptyList(),
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        messages = it.messages + ChatMessage("Waduh, koneksi ke otak gua lagi keganggu nih bre. Coba cek internet lo!", isUser = false),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun resetAssistant() {
        _uiState.value = AssistantUiState()
    }
}
