package id.pusakakata.ui.screens.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class AddEditUiState(
    val term: String = "",
    val definition: String = "",
    val category: String = "Umum",
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val canSave: Boolean get() = term.isNotBlank() && definition.isNotBlank()
}

class AddEditViewModel(
    private val repository: WordRepository,
    private val wordId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        if (wordId != null) {
            loadWord(wordId)
        }
    }

    private fun loadWord(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getWordById(id)?.let { word ->
                _uiState.update { 
                    it.copy(
                        term = word.term,
                        definition = word.definition,
                        category = word.category,
                        isLoading = false
                    )
                }
            } ?: _uiState.update { it.copy(isLoading = false, error = "Kata tidak ditemukan") }
        }
    }

    fun onTermChange(newTerm: String) {
        _uiState.update { it.copy(term = newTerm) }
    }

    fun onDefinitionChange(newDef: String) {
        _uiState.update { it.copy(definition = newDef) }
    }

    fun onCategoryChange(newCat: String) {
        _uiState.update { it.copy(category = newCat) }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun saveWord() {
        if (!_uiState.value.canSave) return
        
        viewModelScope.launch {
            val currentState = _uiState.value
            val word = Word(
                id = wordId ?: Uuid.random().toString(),
                term = currentState.term.trim(),
                definition = currentState.definition.trim(),
                category = currentState.category.trim()
            )
            try {
                if (wordId == null) {
                    repository.insertWord(word)
                } else {
                    repository.updateWord(word)
                }
                _uiState.update { it.copy(isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
