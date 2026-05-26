package com.example.fitgen.presentation.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.domain.model.MealLog
import com.example.fitgen.domain.model.MealType
import com.example.fitgen.domain.usecase.LogMealUseCase
import com.example.fitgen.domain.usecase.AnalyzeFoodNutritionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddMealUiState(
    val namaMakanan: String      = "",
    val kaloriInput: String      = "",
    val proteinInput: String     = "",
    val karbohidratInput: String = "",
    val lemakInput: String       = "",
    val jenisMakan: MealType     = MealType.BREAKFAST,
    val isLoading: Boolean       = false,
    val isSaved: Boolean         = false,
    val errorMessage: String?    = null
)

class AddMealViewModel(
    private val logMealUseCase: LogMealUseCase,
    private val analyzeFoodNutritionUseCase: AnalyzeFoodNutritionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMealUiState())
    val uiState: StateFlow<AddMealUiState> = _uiState.asStateFlow()

    fun onNamaChanged(value: String)        = _uiState.update { it.copy(namaMakanan = value) }
    fun onKaloriChanged(value: String)      = _uiState.update { it.copy(kaloriInput = value) }
    fun onProteinChanged(value: String)     = _uiState.update { it.copy(proteinInput = value) }
    fun onKarbohidratChanged(value: String) = _uiState.update { it.copy(karbohidratInput = value) }
    fun onLemakChanged(value: String)       = _uiState.update { it.copy(lemakInput = value) }
    fun onJenisMakanChanged(type: MealType) = _uiState.update { it.copy(jenisMakan = type) }
    fun clearError()                        = _uiState.update { it.copy(errorMessage = null) }

    fun analyzeFoodWithAI(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = analyzeFoodNutritionUseCase(imageBytes)

            result.fold(
                onSuccess = { nutritionData ->
                    _uiState.update { state ->
                        state.copy(
                            // UPDATE: Langsung gunakan nama dari hasil tebakan AI!
                            namaMakanan = nutritionData.nama,
                            kaloriInput = nutritionData.kalori.toString(),
                            proteinInput = nutritionData.protein.toString(),
                            karbohidratInput = nutritionData.karbohidrat.toString(),
                            lemakInput = nutritionData.lemak.toString(),
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Gagal menganalisis gambar: ${error.message}"
                    ) }
                }
            )
        }
    }

    fun saveMeal() {
        val state  = _uiState.value
        val kalori = state.kaloriInput.trim().toIntOrNull() ?: run {
            _uiState.update { it.copy(errorMessage = "Kalori harus berupa angka") }
            return
        }
        val meal = MealLog(
            namaMakanan  = state.namaMakanan.trim(),
            kalori       = kalori,
            proteinG     = state.proteinInput.trim().toDoubleOrNull() ?: 0.0,
            karbohidratG = state.karbohidratInput.trim().toDoubleOrNull() ?: 0.0,
            lemakG       = state.lemakInput.trim().toDoubleOrNull() ?: 0.0,
            jenisMakan   = state.jenisMakan
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            logMealUseCase(meal)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSaved = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }
}