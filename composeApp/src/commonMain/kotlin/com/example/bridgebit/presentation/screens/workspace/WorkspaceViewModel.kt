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
    private val repository: TranslationRepository
) : ViewModel() {

    var currentTranslationId: Long? = null

    var sourceText = mutableStateOf("")
    var translatedText = mutableStateOf("")
    var sourceLanguage = mutableStateOf("Indonesia")
    var targetLanguage = mutableStateOf("Inggris")
    var category = mutableStateOf("Umum") // <-- STATE KATEGORI BARU

    fun loadTranslation(id: Long) {
        viewModelScope.launch {
            currentTranslationId = id
            val translation = repository.getTranslationById(id).firstOrNull()
            if (translation != null) {
                sourceText.value = translation.sourceText
                translatedText.value = translation.translatedText
                sourceLanguage.value = translation.sourceLanguage
                targetLanguage.value = translation.targetLanguage
                category.value = translation.category // <-- LOAD KATEGORI
            }
        }
    }

    fun saveTranslation(onSaveSuccess: () -> Unit) {
        viewModelScope.launch {
            val newTranslation = Translation(
                id = currentTranslationId ?: 0L,
                sourceText = sourceText.value,
                translatedText = if (translatedText.value.isBlank()) "belum bisa menerjemahkan" else translatedText.value,
                sourceLanguage = sourceLanguage.value,
                targetLanguage = targetLanguage.value,
                category = category.value, // <-- SIMPAN KATEGORI
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
            saveTranslationUseCase(newTranslation)
            onSaveSuccess()
        }
    }
}