package id.my.sinanonym.mybawanggacha.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.my.sinanonym.mybawanggacha.domain.ai.repository.AIRepository
import id.my.sinanonym.mybawanggacha.domain.ai.repository.ChatMessage
import id.my.sinanonym.mybawanggacha.domain.ai.repository.MessageSender
import id.my.sinanonym.mybawanggacha.domain.ai.repository.WritingStyle
import id.my.sinanonym.mybawanggacha.domain.note.usecase.GenerateIdeasUseCase
import id.my.sinanonym.mybawanggacha.domain.note.usecase.ImproveWritingUseCase
import id.my.sinanonym.mybawanggacha.domain.note.usecase.SummarizeNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.repository.SettingsRepository
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiPersonality
import id.my.sinanonym.mybawanggacha.domain.ai.repository.AiChatSessionRepository
import id.my.sinanonym.mybawanggacha.domain.anime.repository.AnimeRepository
import id.my.sinanonym.mybawanggacha.domain.manga.repository.MangaRepository
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType
import id.my.sinanonym.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val summarizeUseCase: SummarizeNoteUseCase,
    private val improveWritingUseCase: ImproveWritingUseCase,
    private val generateIdeasUseCase: GenerateIdeasUseCase,
    private val settingsRepository: SettingsRepository,
    private val chatSessionRepository: AiChatSessionRepository,
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AIAssistantEvent>()
    val events: SharedFlow<AIAssistantEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            settingsRepository.aiApiSettings.collect { settings ->
                _uiState.update { state ->
                    state.copy(
                        aiApiModel = settings.model,
                        aiPersonality = settings.personality
                    )
                }
            }
        }
    }

    fun setInitialText(text: String?) {
        text?.let {
            _uiState.update { state -> state.copy(inputText = it) }
        }
    }

    fun configureSession(
        noteId: Long?,
        mediaId: Int?,
        mediaType: String?,
        mediaTitle: String?,
        context: String?
    ) {
        val sessionKey = buildSessionKey(
            noteId = noteId,
            mediaId = mediaId,
            mediaType = mediaType
        )

        if (_uiState.value.sessionKey == sessionKey && _uiState.value.animeContext == context) {
            return
        }

        _uiState.update { state ->
            state.copy(
                sessionKey = sessionKey,
                sessionTitle = mediaTitle,
                sessionMediaType = mediaType,
                animeContext = context,
                isLoadingSession = true,
                error = null
            )
        }

        viewModelScope.launch {
            val messages = chatSessionRepository.getMessages(sessionKey)
                .map { message ->
                    if (message.sender == MessageSender.AI) {
                        message.copy(text = resolveMediaReferences(message.text))
                    } else {
                        message
                    }
                }
            _uiState.update { state ->
                state.copy(
                    chatHistory = messages,
                    isLoadingSession = false
                )
            }
        }
    }

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }

    fun onActionSelected(action: AIAction) {
        _uiState.update { it.copy(selectedAction = action) }
    }

    fun executeAction() {
        val state = _uiState.value
        val messageText = state.inputText.trim()

        if (messageText.isBlank()) {
            _uiState.update { it.copy(error = "Masukkan teks terlebih dahulu") }
            return
        }

        val userMessage = ChatMessage(sender = MessageSender.USER, text = messageText)
        val updatedHistory = state.chatHistory + userMessage
        _uiState.update {
            it.copy(
                inputText = "",
                isLoading = true,
                error = null,
                chatHistory = updatedHistory
            )
        }

        viewModelScope.launch {
            chatSessionRepository.appendMessage(state.sessionKey, userMessage)

            val contextPrompt = buildScreenContextPrompt(state.animeContext)
            val result = chat(updatedHistory, contextPrompt)

            result
                .onSuccess { output ->
                    val resolvedOutput = resolveMediaReferences(output)
                    val aiMessage = ChatMessage(sender = MessageSender.AI, text = resolvedOutput)
                    chatSessionRepository.appendMessage(state.sessionKey, aiMessage)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            chatHistory = it.chatHistory + aiMessage
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Terjadi kesalahan"
                        )
                    }
                }
        }
    }

    fun resetSession() {
        val sessionKey = _uiState.value.sessionKey
        viewModelScope.launch {
            chatSessionRepository.clearSession(sessionKey)
            _uiState.update {
                it.copy(
                    chatHistory = emptyList(),
                    error = null,
                    isLoading = false
                )
            }
        }
    }

    fun copyResult(text: String) {
        viewModelScope.launch {
            _events.emit(AIAssistantEvent.CopyToClipboard(text))
        }
    }

    fun applyToNote(text: String) {
        viewModelScope.launch {
            _events.emit(AIAssistantEvent.ApplyToNote(text))
        }
    }

    fun onWritingStyleChange(style: WritingStyle) {
        _uiState.update { it.copy(writingStyle = style) }
    }

    fun onTargetLanguageChange(language: String) {
        _uiState.update { it.copy(targetLanguage = language) }
    }

    fun setAiApiModel(aiApiModel: AiApiModel) {
        viewModelScope.launch {
            settingsRepository.setAiApiModel(aiApiModel)
        }
    }

    private suspend fun resolveMediaReferences(text: String): String {
        val blocks = MEDIA_BLOCK_REGEX.findAll(text).toList()
        if (blocks.isEmpty()) return text

        val replacements = supervisorScope {
            blocks.map { match ->
                async {
                    val body = match.groupValues[1]
                    val values = parseMediaReferenceValues(body)
                    val currentImageUrl = values["image_url"]?.takeIf { it.isUsableImageUrl() }
                    val type = values["type"].normalizedMediaType()
                    val title = values["title"].orEmpty()
                    val malId = values["mal_id"]?.toIntOrNull()

                    if (currentImageUrl != null) {
                        match.value to match.value
                    } else {
                        val resolved = resolveMediaReference(
                            type = type,
                            malId = malId,
                            title = title
                        )

                        if (resolved == null) {
                            match.value to match.value
                        } else {
                            match.value to upsertMediaReferenceValues(
                                block = match.value,
                                values = mapOf(
                                    "type" to resolved.type,
                                    "mal_id" to resolved.malId.toString(),
                                    "title" to resolved.title,
                                    "score" to (resolved.score ?: values["score"]).orEmpty(),
                                    "image_url" to resolved.imageUrl.orEmpty()
                                )
                            )
                        }
                    }
                }
            }.awaitAll()
        }

        return replacements.fold(text) { acc, (old, new) ->
            acc.replace(old, new)
        }
    }

    private suspend fun resolveMediaReference(
        type: String,
        malId: Int?,
        title: String
    ): ResolvedMediaReference? {
        if (malId != null) {
            resolveMediaById(type = type, malId = malId)?.let { return it }
        }

        return resolveMediaByTitle(
            type = type,
            title = title
        )
    }

    private suspend fun resolveMediaById(
        type: String,
        malId: Int
    ): ResolvedMediaReference? {
        return runCatching {
            when (type) {
                "anime" -> {
                    val detail = animeRepository.getAnimeDetail(malId).anime
                    ResolvedMediaReference(
                        type = "anime",
                        malId = detail.malId,
                        title = detail.title,
                        score = detail.score?.toString(),
                        imageUrl = detail.imageUrl
                    )
                }
                else -> {
                    val detail = mangaRepository.getMangaDetail(malId)
                    ResolvedMediaReference(
                        type = "manga",
                        malId = detail.malId,
                        title = detail.title,
                        score = detail.score?.toString(),
                        imageUrl = detail.imageUrl
                    )
                }
            }
        }.getOrNull()
            ?.takeIf { it.imageUrl.isUsableImageUrl() }
    }

    private suspend fun resolveMediaByTitle(
        type: String,
        title: String
    ): ResolvedMediaReference? {
        if (title.isBlank()) return null

        val mediaType = if (type == "anime") {
            SearchMediaType.Anime
        } else {
            SearchMediaType.Manga
        }

        return runCatching {
            searchRepository.search(
                filters = MediaSearchFilters(
                    mediaType = mediaType,
                    query = title,
                    limit = "5",
                    sfw = true
                ),
                page = 1
            ).items
                .firstOrNull { item ->
                    item.title.equals(title, ignoreCase = true) ||
                            item.title.contains(title, ignoreCase = true) ||
                            title.contains(item.title, ignoreCase = true)
                }
                ?: searchRepository.search(
                    filters = MediaSearchFilters(
                        mediaType = mediaType,
                        query = title,
                        limit = "5",
                        sfw = true
                    ),
                    page = 1
                ).items.firstOrNull()
        }.getOrNull()
            ?.takeIf { it.imageUrl.isUsableImageUrl() }
            ?.let { item ->
                ResolvedMediaReference(
                    type = if (item.mediaType == SearchMediaType.Anime) "anime" else "manga",
                    malId = item.malId,
                    title = item.title,
                    score = item.score?.toString(),
                    imageUrl = item.imageUrl
                )
            }
    }

    private fun parseMediaReferenceValues(body: String): Map<String, String> {
        return body.lines()
            .mapNotNull { line ->
                val index = line.indexOf("=")
                if (index <= 0) return@mapNotNull null
                line.take(index).trim().lowercase() to line.drop(index + 1).trim()
            }
            .toMap()
    }

    private fun upsertMediaReferenceValues(
        block: String,
        values: Map<String, String>
    ): String {
        val lines = block.lines().toMutableList()

        values.forEach { (key, value) ->
            if (value.isBlank()) return@forEach

            val existingIndex = lines.indexOfFirst { line ->
                line.substringBefore("=", missingDelimiterValue = "")
                    .trim()
                    .equals(key, ignoreCase = true)
            }

            if (existingIndex >= 0) {
                lines[existingIndex] = "$key=$value"
            } else {
                val endIndex = lines.indexOfLast { it.trim() == ":::" }
                    .takeIf { it >= 0 }
                    ?: lines.size
                lines.add(endIndex, "$key=$value")
            }
        }

        return lines.joinToString("\n")
    }

    private fun String?.normalizedMediaType(): String {
        return when (this?.lowercase()?.trim()) {
            "anime" -> "anime"
            else -> "manga"
        }
    }

    private fun String?.isUsableImageUrl(): Boolean {
        val value = this?.trim().orEmpty()
        return value.startsWith("https://") || value.startsWith("http://")
    }

    private fun buildSessionKey(
        noteId: Long?,
        mediaId: Int?,
        mediaType: String?
    ): String {
        return when {
            mediaId != null && !mediaType.isNullOrBlank() -> {
                "${mediaType.uppercase()}:$mediaId"
            }
            noteId != null -> "NOTE:$noteId"
            else -> "GLOBAL"
        }
    }

    private fun buildScreenContextPrompt(animeContext: String?): String? {
        return animeContext
            ?.takeIf { it.isNotBlank() }
            ?.let { context ->
                """
                    Pengguna sedang membuka konteks anime/manga di aplikasi:
                    $context

                    Gunakan konteks ini hanya jika relevan dengan pertanyaan pengguna.
                    Jika data konteks tidak memuat informasi yang ditanyakan, jelaskan bahwa data tersebut tidak tersedia di layar ini.
                """.trimIndent()
            }
    }

    // ==================== AI OPERATIONS ====================

    private suspend fun summarize(text: String): Result<String> {
        return summarizeUseCase(text)
    }

    private suspend fun generateIdeas(topic: String): Result<String> {
        return generateIdeasUseCase(topic).map { ideas ->
            ideas.mapIndexed { index, idea -> "${index + 1}. $idea" }.joinToString("\n")
        }
    }

    private suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        return improveWritingUseCase(text, style)
    }

    private suspend fun translate(text: String, targetLanguage: String): Result<String> {
        return aiRepository.translate(text, targetLanguage)
    }

    private suspend fun suggestTitle(content: String): Result<String> {
        return aiRepository.suggestTitle(content)
    }

    private suspend fun chat(history: List<ChatMessage>, systemPrompt: String? = null): Result<String> {
        return aiRepository.chat(history, systemPrompt)
    }

    private data class ResolvedMediaReference(
        val type: String,
        val malId: Int,
        val title: String,
        val score: String?,
        val imageUrl: String?
    )

    private companion object {
        val MEDIA_BLOCK_REGEX = Regex(
            pattern = ":::media\\s*\\n([\\s\\S]*?)\\n:::",
            option = RegexOption.IGNORE_CASE
        )
    }
}

enum class AIAction(val displayName: String, val description: String) {
    SUMMARIZE("Ringkas", "Buat ringkasan dari teks"),
    GENERATE_IDEAS("Ide", "Generate ide berdasarkan topik"),
    IMPROVE_WRITING("Perbaiki", "Perbaiki tulisan"),
    TRANSLATE("Terjemah", "Terjemahkan ke bahasa lain"),
    SUGGEST_TITLE("Judul", "Sarankan judul"),
    CHAT("Tanya", "Tanya AI tentang apapun")
}


data class AIAssistantUiState(
    val inputText: String = "",
    val selectedAction: AIAction = AIAction.CHAT,
    val writingStyle: WritingStyle = WritingStyle.NEUTRAL,
    val targetLanguage: String = "English",
    val aiApiModel: AiApiModel = AiApiModel.Gemini35Flash,
    val aiPersonality: AiPersonality = AiPersonality.Default,
    val isLoading: Boolean = false,
    val isLoadingSession: Boolean = false,
    val sessionKey: String = "GLOBAL",
    val sessionTitle: String? = null,
    val sessionMediaType: String? = null,
    val result: String? = null,
    val error: String? = null,
    val chatHistory: List<ChatMessage> = emptyList(),
    val animeContext: String? = null
) {
    val canExecute: Boolean
        get() = inputText.isNotBlank() && !isLoading
}

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyToNote(val text: String) : AIAssistantEvent
}