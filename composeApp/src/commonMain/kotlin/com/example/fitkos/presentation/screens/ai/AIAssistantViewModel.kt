package com.example.fitkos.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitkos.domain.repository.AIRepository
import com.example.fitkos.domain.repository.WritingStyle
import com.example.fitkos.domain.usecase.GenerateIdeasUseCase
import com.example.fitkos.domain.usecase.ImproveWritingUseCase
import com.example.fitkos.domain.usecase.SummarizeNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val summarizeUseCase: SummarizeNoteUseCase,
    private val improveWritingUseCase: ImproveWritingUseCase,
    private val generateIdeasUseCase: GenerateIdeasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AIAssistantEvent>()
    val events: SharedFlow<AIAssistantEvent> = _events.asSharedFlow()

    fun setInitialText(text: String?) {
        text?.let {
            _uiState.update { state -> state.copy(inputText = it) }
            if (it.isNotBlank()) {
                executeAction()
            }
        }
    }

    fun onInputTextChange(text: String) {
        _uiState.update {
            it.copy(
                inputText = text,
                error = null,
                cacheNotice = null
            )
        }
    }

    fun onActionSelected(action: AIAction) {
        _uiState.update { it.copy(selectedAction = action) }
    }

    fun executeAction() {
        val state = _uiState.value
        val messageText = state.inputText.trim()

        if (messageText.isBlank()) {
            _uiState.update { it.copy(error = "Masukkan pesan") }
            return
        }

        val userMessage = Message(
            text = messageText,
            isFromUser = true
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                isRefreshing = false,
                isShowingCachedResponse = false,
                cacheNotice = null,
                error = null
            )
        }

        viewModelScope.launch {
            if (state.selectedAction == AIAction.CHAT) {
                executeChatWithStaleWhileRevalidate(messageText)
            } else {
                executeRegularAction(
                    action = state.selectedAction,
                    messageText = messageText,
                    writingStyle = state.writingStyle,
                    targetLanguage = state.targetLanguage
                )
            }
        }
    }

    private suspend fun executeChatWithStaleWhileRevalidate(messageText: String) {
        val cachedResponse = aiRepository.getCachedChatResponse().first()
        val canShowCache = cachedResponse.isAvailable && cachedResponse.prompt == messageText

        if (canShowCache) {
            val cachedMessage = Message(
                text = """
                    Rekomendasi terakhir:
                    
                    ${cachedResponse.response}
                """.trimIndent(),
                isFromUser = false
            )

            _uiState.update {
                it.copy(
                    messages = it.messages + cachedMessage,
                    result = cachedResponse.response,
                    isLoading = true,
                    isRefreshing = true,
                    isShowingCachedResponse = true,
                    cacheNotice = "Menampilkan rekomendasi terakhir. Sedang memperbarui..."
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = true,
                    cacheNotice = "Mengambil rekomendasi dari AI..."
                )
            }
        }

        val result = aiRepository.refreshChat(messageText)

        result
            .onSuccess { output ->
                val aiMessage = Message(
                    text = if (canShowCache) {
                        """
                            Rekomendasi terbaru:
                            
                            $output
                        """.trimIndent()
                    } else {
                        output
                    },
                    isFromUser = false
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isShowingCachedResponse = false,
                        cacheNotice = if (canShowCache) {
                            "Rekomendasi berhasil diperbarui."
                        } else {
                            "Rekomendasi berhasil dimuat."
                        },
                        error = null,
                        messages = it.messages + aiMessage,
                        result = output
                    )
                }
            }
            .onFailure { error ->
                val message = if (canShowCache) {
                    "Gagal memperbarui. Menampilkan rekomendasi terakhir."
                } else {
                    error.message ?: "Gagal mengambil rekomendasi AI"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isShowingCachedResponse = canShowCache,
                        cacheNotice = if (canShowCache) message else null,
                        error = message
                    )
                }
            }
    }

    private suspend fun executeRegularAction(
        action: AIAction,
        messageText: String,
        writingStyle: WritingStyle,
        targetLanguage: String
    ) {
        val result = when (action) {
            AIAction.SUMMARIZE -> summarize(messageText)
            AIAction.GENERATE_IDEAS -> generateIdeas(messageText)
            AIAction.IMPROVE_WRITING -> improveWriting(messageText, writingStyle)
            AIAction.TRANSLATE -> translate(messageText, targetLanguage)
            AIAction.SUGGEST_TITLE -> suggestTitle(messageText)
            AIAction.CHAT -> chat(messageText)
        }

        result
            .onSuccess { output ->
                val aiMessage = Message(
                    text = output,
                    isFromUser = false
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        messages = it.messages + aiMessage,
                        result = output,
                        cacheNotice = null,
                        error = null
                    )
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = error.message ?: "Terjadi kesalahan"
                    )
                }
            }
    }

    fun copyResult() {
        val result = _uiState.value.result
        if (result != null) {
            viewModelScope.launch {
                _events.emit(AIAssistantEvent.CopyToClipboard(result))
            }
        }
    }

    fun applyToNote() {
        val result = _uiState.value.result
        if (result != null) {
            viewModelScope.launch {
                _events.emit(AIAssistantEvent.ApplyToNote(result))
            }
        }
    }

    fun onWritingStyleChange(style: WritingStyle) {
        _uiState.update { it.copy(writingStyle = style) }
    }

    fun onTargetLanguageChange(language: String) {
        _uiState.update { it.copy(targetLanguage = language) }
    }

    private suspend fun summarize(text: String): Result<String> {
        return summarizeUseCase(text)
    }

    private suspend fun generateIdeas(topic: String): Result<String> {
        return generateIdeasUseCase(topic).map { ideas ->
            ideas.mapIndexed { index, idea ->
                "${index + 1}. $idea"
            }.joinToString("\n")
        }
    }

    private suspend fun improveWriting(
        text: String,
        style: WritingStyle
    ): Result<String> {
        return improveWritingUseCase(text, style)
    }

    private suspend fun translate(
        text: String,
        targetLanguage: String
    ): Result<String> {
        return aiRepository.translate(text, targetLanguage)
    }

    private suspend fun suggestTitle(content: String): Result<String> {
        return aiRepository.suggestTitle(content)
    }

    private suspend fun chat(message: String): Result<String> {
        return aiRepository.chat(message)
    }
}

enum class AIAction(
    val displayName: String,
    val description: String
) {
    SUMMARIZE("Evaluasi", "Evaluasi data atau catatan harian"),
    GENERATE_IDEAS("Ide Menu", "Beri ide menu sehat hemat"),
    IMPROVE_WRITING("Rapikan", "Rapikan catatan makanan"),
    TRANSLATE("Terjemah", "Terjemahkan teks"),
    SUGGEST_TITLE("Judul", "Sarankan judul catatan"),
    CHAT("Tanya FitKos", "Tanya AI tentang hidup sehat anak kos")
}

data class Message(
    val text: String,
    val isFromUser: Boolean
)

data class AIAssistantUiState(
    val inputText: String = "",
    val messages: List<Message> = emptyList(),
    val selectedAction: AIAction = AIAction.CHAT,
    val writingStyle: WritingStyle = WritingStyle.NEUTRAL,
    val targetLanguage: String = "English",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isShowingCachedResponse: Boolean = false,
    val cacheNotice: String? = null,
    val result: String? = null,
    val error: String? = null
) {
    val canExecute: Boolean
        get() = inputText.isNotBlank() && !isLoading
}

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyToNote(val text: String) : AIAssistantEvent
}