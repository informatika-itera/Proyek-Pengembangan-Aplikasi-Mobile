package com.example.synesthesia.presentation.screens.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import com.example.synesthesia.domain.model.EmotionSystem
import com.example.synesthesia.domain.model.EmotionCategory
import com.example.synesthesia.domain.repository.NoteRepository
import com.example.synesthesia.domain.usecase.SaveNoteUseCase
import com.example.synesthesia.data.remote.api.GeminiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class AddNoteViewModel(
    private val repository: NoteRepository,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val geminiService: GeminiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddNoteUiState())
    val uiState: StateFlow<AddNoteUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AddNoteEvent>()
    val events: SharedFlow<AddNoteEvent> = _events.asSharedFlow()
    
    private var currentNoteId: Long? = null
    
    fun loadNote(noteId: Long) {
        currentNoteId = noteId
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            repository.getNoteById(noteId).collect { note ->
                note?.let {
                    _uiState.update { state ->
                        state.copy(
                            title = note.title,
                            content = note.content,
                            category = note.category,
                            color = note.color,
                            emotion = note.emotion,
                            artToken = note.artToken,
                            aiResonance = note.aiResonance,
                            isLoading = false,
                            isEditMode = true,
                            createdAt = note.createdAt,
                            selectedMainCategory = EmotionSystem.categories.find { it.name == note.artToken },
                            selectedSubEmotion = note.emotion
                        )
                    }
                }
            }
        }
    }
    
    // ==================== USER ACTIONS ====================
    
    fun onContentChange(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun onMainCategorySelected(category: EmotionCategory?) {
        _uiState.update { it.copy(
            selectedMainCategory = category,
            selectedSubEmotion = null,
            artToken = category?.name
        ) }
    }
    
    fun onSubEmotionSelected(subEmotion: String?) {
        _uiState.update { it.copy(
            selectedSubEmotion = subEmotion,
            emotion = subEmotion
        ) }
    }
    
    fun toggleParaphrase() {
        _uiState.update { it.copy(isParaphraseEnabled = !it.isParaphraseEnabled) }
    }
    
    
    fun saveNote() {
        val state = _uiState.value
        
        if (state.content.length < 5) {
            viewModelScope.launch {
                _events.emit(AddNoteEvent.Error("Tuliskan minimal 5 karakter curhatanmu"))
            }
            return
        }

        _uiState.update { it.copy(isAnalyzing = true) }
        
        viewModelScope.launch {
            // Step 1: Analyze with Gemini
            val analysisResult = geminiService.analyzeEmotion(state.content)
            
            analysisResult.onSuccess { analysis ->
                _uiState.update { it.copy(isAnalyzing = false, isSaving = true) }
                
                // Find category by ID returned from AI (HEP, HEU, LEP, LEU)
                val category = EmotionSystem.categories.find { it.id == analysis.emotionQuadrant }
                
                val note = Note(
                    id = currentNoteId ?: 0,
                    title = analysis.autoTitle,
                    content = if (state.isParaphraseEnabled) analysis.paraphrasedContent else state.content,
                    category = state.category,
                    color = state.color,
                    emotion = analysis.subEmotion,
                    artToken = category?.name ?: EmotionSystem.categories.first().name, // The hub name for graph
                    aiResonance = analysis.summary,
                    createdAt = if (currentNoteId == null) Clock.System.now() else state.createdAt,
                    updatedAt = Clock.System.now()
                )
                
                saveNoteUseCase(note)
                    .onSuccess {
                        _events.emit(AddNoteEvent.NoteSaved)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isSaving = false) }
                        _events.emit(AddNoteEvent.Error(error.message ?: "Gagal menyimpan"))
                    }
            }.onFailure {
                // FALLBACK: Save without AI if analysis fails
                _uiState.update { it.copy(isAnalyzing = false, isSaving = true) }
                _events.emit(AddNoteEvent.Error("AI offline: Menggunakan mode hemat daya."))
                
                val fallbackNote = Note(
                    id = currentNoteId ?: 0,
                    title = if (state.content.length > 20) state.content.take(20) + "..." else state.content,
                    content = state.content,
                    category = state.category,
                    color = state.color,
                    emotion = state.selectedSubEmotion ?: "Calm",
                    artToken = state.selectedMainCategory?.name ?: EmotionSystem.categories.first().name,
                    aiResonance = "Saved in offline mode.",
                    createdAt = if (currentNoteId == null) Clock.System.now() else state.createdAt,
                    updatedAt = Clock.System.now()
                )

                saveNoteUseCase(fallbackNote)
                    .onSuccess {
                        _events.emit(AddNoteEvent.NoteSaved)
                    }
                    .onFailure { saveError ->
                        _uiState.update { it.copy(isSaving = false) }
                        _events.emit(AddNoteEvent.Error("Gagal menyimpan: ${saveError.message}"))
                    }
            }
        }
    }
}

data class AddNoteUiState(
    val title: String = "",
    val content: String = "",
    val category: NoteCategory = NoteCategory.GENERAL,
    val color: NoteColor = NoteColor.DEFAULT,
    val emotion: String? = null,
    val artToken: String? = null,
    val aiResonance: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isEditMode: Boolean = false,
    val titleError: String? = null,
    val createdAt: Instant = Clock.System.now(),
    val selectedMainCategory: EmotionCategory? = null,
    val selectedSubEmotion: String? = null,
    val isParaphraseEnabled: Boolean = true
) {
    val isValid: Boolean
        get() = title.isNotBlank() || content.isNotBlank()
    
    val canSave: Boolean
        get() = isValid && !isSaving && !isAnalyzing
}

sealed interface AddNoteEvent {
    data object NoteSaved : AddNoteEvent
    data class Error(val message: String) : AddNoteEvent
}
