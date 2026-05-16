package com.example.bridgebit.presentation.screens.workspace

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.TranslationRepository
import com.example.bridgebit.domain.usecase.SaveTranslationUseCase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class WorkspaceViewModel(
    private val saveTranslationUseCase: SaveTranslationUseCase,
    private val repository: TranslationRepository // Tambahkan repository untuk Read by ID
) : ViewModel() {

    var currentTranslationId: Long? = null // Penanda apakah ini mode Edit atau Create

    var sourceText = mutableStateOf("")
    var translatedText = mutableStateOf("")
    var sourceLanguage = mutableStateOf("Indonesia")
    var targetLanguage = mutableStateOf("Inggris")

    // Fungsi baru untuk memuat data saat mode Edit
    fun loadTranslation(id: Long) {
        viewModelScope.launch {
            currentTranslationId = id
            val translation = repository.getTranslationById(id).firstOrNull()
            if (translation != null) {
                sourceText.value = translation.sourceText
                translatedText.value = translation.translatedText
                sourceLanguage.value = translation.sourceLanguage
                targetLanguage.value = translation.targetLanguage
            }
        }
    }

    fun saveTranslation(onSaveSuccess: () -> Unit) {
        viewModelScope.launch {
            val newTranslation = Translation(
                id = currentTranslationId ?: 0L, // Jika null berarti ID 0 (Insert), jika ada ID (Update)
                sourceText = sourceText.value,
                translatedText = if (translatedText.value.isBlank()) "belum bisa menerjemahkan" else translatedText.value,
                sourceLanguage = sourceLanguage.value,
                targetLanguage = targetLanguage.value,
                createdAt = Clock.System.now().toEpochMilliseconds(), // Di dunia nyata, createdAt dipertahankan saat update
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
            saveTranslationUseCase(newTranslation)
            onSaveSuccess()
        }
    }
}