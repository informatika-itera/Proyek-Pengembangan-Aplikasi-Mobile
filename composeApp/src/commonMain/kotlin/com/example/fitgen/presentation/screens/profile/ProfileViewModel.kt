package com.example.fitgen.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.data.local.datastore.UserPreferences
import com.example.fitgen.domain.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val age: String = "",
    val height: String = "",
    val weight: String = "",
    val goal: String = "",
    val nameError: String? = null,
    val ageError: String? = null,
    val heightError: String? = null,
    val weightError: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Opsi tujuan (goals) standar
    val goalOptions = listOf(
        "Menurunkan Berat Badan",
        "Membangun Otot",
        "Meningkatkan Stamina",
        "Fleksibilitas",
        "Kebugaran Umum"
    )

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val profile = userPreferences.userProfile.first()
            _uiState.update {
                it.copy(
                    name = profile.name,
                    age = if (profile.age > 0) profile.age.toString() else "",
                    height = if (profile.heightCm > 0.0) profile.heightCm.toString() else "",
                    weight = if (profile.weightKg > 0.0) profile.weightKg.toString() else "",
                    goal = profile.goal,
                    isLoading = false
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = if (value.isBlank()) "Nama tidak boleh kosong" else null) }
    }

    fun onAgeChange(value: String) {
        val error = validateInt(value, "Umur")
        _uiState.update { it.copy(age = value, ageError = error) }
    }

    fun onHeightChange(value: String) {
        val error = validateDouble(value, "Tinggi badan")
        _uiState.update { it.copy(height = value, heightError = error) }
    }

    fun onWeightChange(value: String) {
        val error = validateDouble(value, "Berat badan")
        _uiState.update { it.copy(weight = value, weightError = error) }
    }

    fun onGoalChange(value: String) {
        _uiState.update { it.copy(goal = value) }
    }

    fun saveProfile() {
        val state = _uiState.value

        // Validasi
        val nameErr = if (state.name.isBlank()) "Nama tidak boleh kosong" else null
        val ageErr = validateInt(state.age, "Umur")
        val heightErr = validateDouble(state.height, "Tinggi badan")
        val weightErr = validateDouble(state.weight, "Berat badan")

        if (nameErr != null || ageErr != null || heightErr != null || weightErr != null) {
            _uiState.update {
                it.copy(
                    nameError = nameErr,
                    ageError = ageErr,
                    heightError = heightErr,
                    weightError = weightErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val profile = UserProfile(
                name = state.name.trim(),
                age = state.age.trim().toIntOrNull() ?: 0,
                heightCm = state.height.trim().toDoubleOrNull() ?: 0.0,
                weightKg = state.weight.trim().toDoubleOrNull() ?: 0.0,
                goal = state.goal
            )
            
            userPreferences.saveUserProfile(profile)
            
            // Sedikit delay simulasi save
            delay(500)
            
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    private fun validateInt(value: String, fieldName: String): String? {
        if (value.isBlank()) return "$fieldName tidak boleh kosong"
        val intVal = value.toIntOrNull() ?: return "$fieldName harus berupa angka"
        if (intVal <= 0) return "$fieldName harus lebih dari 0"
        return null
    }

    private fun validateDouble(value: String, fieldName: String): String? {
        if (value.isBlank()) return "$fieldName tidak boleh kosong"
        val doubleVal = value.toDoubleOrNull() ?: return "$fieldName harus berupa angka"
        if (doubleVal <= 0) return "$fieldName harus lebih dari 0"
        return null
    }
}
