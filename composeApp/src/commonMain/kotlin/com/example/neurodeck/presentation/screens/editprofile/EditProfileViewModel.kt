package com.example.neurodeck.presentation.screens.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state untuk EditProfile form.
 *
 * Field-field di sini mirror UserProfile (kecuali memberSince yang
 * tidak editable). Bedanya: ini editable & validation states.
 *
 * canSave = name non-blank + tidak sedang save + tidak loading.
 */
data class EditProfileUiState(
    val isLoading: Boolean = true,
    val name: String = "",
    val username: String = "",
    val bio: String = "",
    val avatarUri: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,   // trigger navigation back
) {
    val canSave: Boolean
        get() = name.isNotBlank() && !isSaving && !isLoading
}

/**
 * ViewModel untuk EditProfile screen.
 *
 * Flow:
 *   init        → load existing profile dari repository → fill form
 *   user edit   → update local state (no persist yet)
 *   save        → persist via repository → emit isSaved=true → Screen navigate back
 */
class EditProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    // Hold original memberSince supaya tidak hilang saat save (UserProfile
    // butuh field ini, dan EditProfile UI tidak expose untuk edit).
    private var originalMemberSince = kotlinx.datetime.Clock.System.now()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val profile = userPreferencesRepository.getProfile()
                originalMemberSince = profile.memberSince
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = profile.name,
                        username = profile.username,
                        bio = profile.bio,
                        avatarUri = profile.avatarUri,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat profil: ${e.message ?: "unknown"}",
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(
                name = value.take(UserProfile.MAX_NAME_LENGTH),
                errorMessage = null,
            )
        }
    }

    fun onUsernameChange(value: String) {
        // Auto-prepend "@" kalau user belum ketik
        val sanitized = if (value.startsWith("@") || value.isEmpty()) value else "@$value"
        _uiState.update {
            it.copy(
                username = sanitized.take(UserProfile.MAX_USERNAME_LENGTH),
                errorMessage = null,
            )
        }
    }

    fun onBioChange(value: String) {
        _uiState.update {
            it.copy(
                bio = value.take(UserProfile.MAX_BIO_LENGTH),
                errorMessage = null,
            )
        }
    }

    fun save() {
        val current = _uiState.value
        if (!current.canSave) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                userPreferencesRepository.saveProfile(
                    UserProfile(
                        name = current.name.trim(),
                        username = current.username.trim().ifBlank { UserProfile.DEFAULT_USERNAME },
                        bio = current.bio.trim(),
                        avatarUri = current.avatarUri,
                        memberSince = originalMemberSince,
                    ),
                )
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Gagal menyimpan: ${e.message ?: "unknown"}",
                    )
                }
            }
        }
    }
}