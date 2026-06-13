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
    val gender: String = "",
    val height: String = "",
    val weight: String = "",
    val goal: String = "",
    val profileImageBytes: ByteArray? = null,
    val weightHistory: List<Double> = emptyList(),
    val nameError: String? = null,
    val ageError: String? = null,
    val genderError: String? = null,
    val heightError: String? = null,
    val weightError: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isLoading: Boolean = true,
    val isDarkMode: Boolean = false
)

class ProfileViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val goalOptions = listOf(
        "Menurunkan Berat Badan",
        "Membangun Otot",
        "Meningkatkan Stamina",
        "Fleksibilitas",
        "Kebugaran Umum"
    )

    init {
        loadProfile()
        viewModelScope.launch {
            userPreferences.isDarkMode.collect { mode ->
                _uiState.update { it.copy(isDarkMode = mode) }
            }
        }
        viewModelScope.launch {
            userPreferences.userProfileImageBase64.collect { base64 ->
                if (base64 != null) {
                    try {
                        @OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
                        val bytes = kotlin.io.encoding.Base64.decode(base64)
                        _uiState.update { it.copy(profileImageBytes = bytes) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val profile = userPreferences.userProfile.first()
            val currentWeight = profile.weightKg

            val initialHistory = if (currentWeight > 0.0) {
                listOf(currentWeight - 2.0, currentWeight - 1.5, currentWeight - 0.5, currentWeight)
            } else emptyList()

            _uiState.update {
                it.copy(
                    name = profile.name,
                    age = if (profile.age > 0) profile.age.toString() else "",
                    gender = profile.gender,
                    height = if (profile.heightCm > 0.0) profile.heightCm.toString() else "",
                    weight = if (currentWeight > 0.0) currentWeight.toString() else "",
                    weightHistory = initialHistory,
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

    fun onGenderChange(value: String) {
        _uiState.update { it.copy(gender = value, genderError = if (value.isBlank()) "Jenis kelamin harus dipilih" else null) }
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

    fun onProfileImageChange(bytes: ByteArray?) {
        _uiState.update { it.copy(profileImageBytes = bytes) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun saveProfile() {
        val state = _uiState.value

        val nameErr = if (state.name.isBlank()) "Nama tidak boleh kosong" else null
        val genderErr = if (state.gender.isBlank()) "Jenis kelamin harus dipilih" else null
        val heightErr = validateDouble(state.height, "Tinggi badan")
        val weightErr = validateDouble(state.weight, "Berat badan")

        if (nameErr != null || genderErr != null || heightErr != null || weightErr != null) {
            _uiState.update {
                it.copy(
                    nameError = nameErr,
                    genderError = genderErr,
                    heightError = heightErr,
                    weightError = weightErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val w = state.weight.trim().toDoubleOrNull() ?: 0.0
            val h = state.height.trim().toDoubleOrNull() ?: 0.0
            val profileToSave = UserProfile(
                name = state.name.trim(),
                age = state.age.trim().toIntOrNull() ?: 0,
                gender = state.gender,
                heightCm = h,
                weightKg = w,
                goal = state.goal
            )

            userPreferences.saveUserProfile(profileToSave)

            if (state.profileImageBytes != null) {
                try {
                    @OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
                    val base64 = kotlin.io.encoding.Base64.encode(state.profileImageBytes)
                    userPreferences.saveUserProfileImageBase64(base64)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val updatedHistory = state.weightHistory.toMutableList().apply {
                if (w > 0.0) add(w)
            }

            delay(500)

            _uiState.update {
                it.copy(
                    isSaving = false,
                    saveSuccess = true,
                    weightHistory = updatedHistory
                )
            }
        }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userPreferences.logout()
            onSuccess()
        }
    }

    private fun validateInt(value: String, fieldName: String): String? {
        if (value.isBlank()) return "$fieldName tidak boleh kosong"
        val intVal = value.toIntOrNull() ?: return "$fieldName harus berupa angka"
        if (intVal <= 0) return "$fieldName harus lebih dari 0"
        if (fieldName == "Umur" && intVal > 120) return "$fieldName maksimal 120"
        return null
    }

    private fun validateDouble(value: String, fieldName: String): String? {
        if (value.isBlank()) return "$fieldName tidak boleh kosong"
        val doubleVal = value.toDoubleOrNull() ?: return "$fieldName harus berupa angka"
        if (doubleVal <= 0) return "$fieldName harus lebih dari 0"
        if (fieldName == "Berat badan" && doubleVal > 500) return "$fieldName maksimal 500 kg"
        if (fieldName == "Tinggi badan" && doubleVal > 300) return "$fieldName maksimal 300 cm"
        return null
    }
}