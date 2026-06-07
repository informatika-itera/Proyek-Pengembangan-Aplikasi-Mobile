package com.example.synesthesia.fakes

import com.example.synesthesia.data.local.datastore.UserPreferences
import com.example.synesthesia.data.remote.dto.EmotionAnalysisResponse
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.repository.AIRepository
import com.example.synesthesia.domain.repository.NoteRepository
import com.example.synesthesia.domain.repository.WritingStyle
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeNoteRepository : NoteRepository {
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L

    // Configurable error simulation
    var shouldThrowOnInsert = false
    var shouldThrowOnUpdate = false
    var shouldThrowOnDelete = false
    var errorMessage = "Simulated repository error"
    
    override fun getAllNotes(): Flow<List<Note>> = notes
    
    override fun getPinnedNotes(): Flow<List<Note>> {
        return notes.map { list -> list.filter { it.isPinned } }
    }
    
    override fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> {
        return notes.map { list -> list.filter { it.category == category } }
    }
    
    override fun searchNotes(query: String): Flow<List<Note>> {
        return notes.map { list ->
            list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }
    }
    
    override fun getNoteById(id: Long): Flow<Note?> {
        return notes.map { list -> list.find { it.id == id } }
    }
    
    override suspend fun insertNote(note: Note): Long {
        if (shouldThrowOnInsert) throw Exception(errorMessage)
        val id = nextId++
        val newNote = note.copy(id = id)
        notes.update { it + newNote }
        return id
    }
    
    override suspend fun updateNote(note: Note) {
        if (shouldThrowOnUpdate) throw Exception(errorMessage)
        notes.update { list ->
            list.map { if (it.id == note.id) note else it }
        }
    }
    
    override suspend fun deleteNote(id: Long) {
        if (shouldThrowOnDelete) throw Exception(errorMessage)
        notes.update { list -> list.filter { it.id != id } }
    }
    
    override suspend fun togglePinNote(id: Long) {
        notes.update { list ->
            list.map { 
                if (it.id == id) it.copy(isPinned = !it.isPinned) else it 
            }
        }
    }
    
    override suspend fun deleteNotes(ids: List<Long>) {
        notes.update { list -> list.filter { it.id !in ids } }
    }
}

class FakeUserPreferences : UserPreferences {
    private val _themeMode = MutableStateFlow("NORMAL")
    private val _userName = MutableStateFlow("Test Stargazer")
    private val _userBio = MutableStateFlow("Test Bio")
    private val _userPhotoUri = MutableStateFlow<String?>(null)
    private val _sortBy = MutableStateFlow("UPDATED_DESC")
    private val _isDarkMode = MutableStateFlow(false)
    private val _isOnboardingCompleted = MutableStateFlow(true)
    private val _defaultCategory = MutableStateFlow("GENERAL")
    private val _showPreview = MutableStateFlow(true)

    override val isDarkMode: Flow<Boolean> = _isDarkMode
    override suspend fun setDarkMode(enabled: Boolean) { _isDarkMode.value = enabled }

    override val themeMode: Flow<String> = _themeMode
    override suspend fun setThemeMode(mode: String) { _themeMode.value = mode }

    override val userName: Flow<String> = _userName
    override val userBio: Flow<String> = _userBio
    override val userPhotoUri: Flow<String?> = _userPhotoUri
    override suspend fun updateProfile(name: String, bio: String, photoUri: String?) { 
        _userName.value = name
        _userBio.value = bio
        photoUri?.let { _userPhotoUri.value = it }
    }

    override val sortBy: Flow<String> = _sortBy
    override suspend fun setSortBy(sortBy: String) { _sortBy.value = sortBy }

    override val defaultCategory: Flow<String> = _defaultCategory
    override suspend fun setDefaultCategory(category: String) { _defaultCategory.value = category }

    override val showPreview: Flow<Boolean> = _showPreview
    override suspend fun setShowPreview(show: Boolean) { _showPreview.value = show }

    override val isOnboardingCompleted: Flow<Boolean> = _isOnboardingCompleted
    override suspend fun setOnboardingCompleted() { _isOnboardingCompleted.value = true }
}

/**
 * FakeAIRepository for unit testing AI-dependent use cases and ViewModels.
 * Supports dynamic success/failure toggling per-method.
 */
class FakeAIRepository : AIRepository {
    var summarizeResult: Result<String> = Result.success("Fake summary")
    var generateIdeasResult: Result<List<String>> = Result.success(listOf("Idea 1", "Idea 2", "Idea 3"))
    var improveWritingResult: Result<String> = Result.success("Improved text")
    var translateResult: Result<String> = Result.success("Translated text")
    var chatResult: Result<String> = Result.success("Chat response")
    var suggestTitleResult: Result<String> = Result.success("Suggested Title")
    var analyzeEmotionResult: Result<EmotionAnalysisResponse> = Result.success(
        EmotionAnalysisResponse(
            autoTitle = "Test Title",
            paraphrasedContent = "Test paraphrased content",
            emotionQuadrant = "HEP",
            subEmotion = "Enthusiastic",
            artColorHex = "#FFC107",
            sentiment = "positive",
            emotionScore = 80,
            summary = "A warm and positive entry."
        )
    )

    override suspend fun summarize(text: String): Result<String> = summarizeResult
    override suspend fun generateIdeas(topic: String): Result<List<String>> = generateIdeasResult
    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> = improveWritingResult
    override suspend fun translate(text: String, targetLanguage: String): Result<String> = translateResult
    override suspend fun chat(message: String): Result<String> = chatResult
    override suspend fun suggestTitle(content: String): Result<String> = suggestTitleResult
    override suspend fun analyzeEmotion(text: String): Result<EmotionAnalysisResponse> = analyzeEmotionResult
}

/**
 * FakeGeminiService that can simulate success/failure for ViewModel tests.
 * We use this instead of the real GeminiService which requires HttpClient.
 */
class FakeGeminiService {
    var generateContentResult: Result<String> = Result.success("Generated content")
    var analyzeEmotionResult: Result<EmotionAnalysisResponse> = Result.success(
        EmotionAnalysisResponse(
            autoTitle = "Fake Title",
            paraphrasedContent = "Fake paraphrased",
            emotionQuadrant = "HEP",
            subEmotion = "Enthusiastic",
            artColorHex = "#FFC107",
            sentiment = "positive",
            emotionScore = 85,
            summary = "A beautiful entry."
        )
    )

    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = generateContentResult

    suspend fun analyzeEmotion(journalText: String): Result<EmotionAnalysisResponse> = analyzeEmotionResult
}
