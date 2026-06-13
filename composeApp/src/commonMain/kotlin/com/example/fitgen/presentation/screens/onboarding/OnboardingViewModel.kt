package com.example.fitgen.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.data.local.datastore.UserPreferences
import com.example.fitgen.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val weight: String = "",
    val height: String = "",
    val profileImageBytes: ByteArray? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() && age.isNotBlank() && gender.isNotBlank() && weight.isNotBlank() && height.isNotBlank()
}

class OnboardingViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateAge(age: String) {
        _uiState.update { it.copy(age = age) }
    }

    fun updateGender(gender: String) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun updateWeight(weight: String) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun updateHeight(height: String) {
        _uiState.update { it.copy(height = height) }
    }

    fun onProfileImageChange(bytes: ByteArray) {
        _uiState.update { it.copy(profileImageBytes = bytes) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun submitProfile() {
        if (!_uiState.value.isFormValid) {
            _uiState.update { it.copy(error = "Mohon lengkapi semua data.") }
            return
        }

        val age = _uiState.value.age.toIntOrNull()
        if (age == null || age <= 0 || age > 120) {
            _uiState.update { it.copy(error = "Usia tidak valid. Masukkan angka yang benar.") }
            return
        }

        val weight = _uiState.value.weight.toDoubleOrNull()
        if (weight == null || weight <= 0.0 || weight > 500.0) {
            _uiState.update { it.copy(error = "Berat badan tidak valid.") }
            return
        }

        val height = _uiState.value.height.toDoubleOrNull()
        if (height == null || height <= 0.0 || height > 300.0) {
            _uiState.update { it.copy(error = "Tinggi badan tidak valid.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val profile = UserProfile(
                name = _uiState.value.name,
                age = age,
                gender = _uiState.value.gender,
                heightCm = height,
                weightKg = weight,
                goal = "Menjadi lebih sehat" // Default goal
            )

            _uiState.value.profileImageBytes?.let { bytes ->
                @OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
                val base64String = kotlin.io.encoding.Base64.encode(bytes)
                userPreferences.saveUserProfileImageBase64(base64String)
            }

            userPreferences.saveUserProfile(profile)
            userPreferences.setOnboardingCompleted()

            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }
}
