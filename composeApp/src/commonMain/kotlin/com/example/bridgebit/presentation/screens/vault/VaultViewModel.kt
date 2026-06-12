package com.example.bridgebit.presentation.screens.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.usecase.GetVaultPhrasesUseCase
import com.example.bridgebit.domain.usecase.ToggleVaultStatusUseCase
import com.example.bridgebit.domain.usecase.DeleteTranslationUseCase // <-- Import Baru
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaultViewModel(
    getVaultPhrasesUseCase: GetVaultPhrasesUseCase,
    private val toggleVaultStatusUseCase: ToggleVaultStatusUseCase,
    private val deleteTranslationUseCase: DeleteTranslationUseCase
) : ViewModel() {

    val groupedVaultPhrases: StateFlow<Map<String, List<Translation>>> = getVaultPhrasesUseCase()
        .map { phrases -> phrases.groupBy { it.category } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun unvaultTranslation(id: Long) {
        viewModelScope.launch { toggleVaultStatusUseCase(id) }
    }


    fun deleteTranslation(id: Long) {
        viewModelScope.launch { deleteTranslationUseCase(id) }
    }
}