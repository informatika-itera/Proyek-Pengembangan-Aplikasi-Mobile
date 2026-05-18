package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.domain.model.Profile
import com.kosthub.app.domain.repository.ProfileRepository
import com.kosthub.app.presentation.state.OperationState
import com.kosthub.app.presentation.state.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _uiState = MutableStateFlow<UiState<Profile>>(UiState.Loading)
    val uiState: StateFlow<UiState<Profile>> = _uiState

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState

    init {
        loadProfile()
    }

    fun loadProfile() {
        scope.launch {
            _uiState.value = UiState.Loading
            try {
                val profile = repository.getProfile() ?: defaultProfile().also {
                    repository.saveProfile(it)
                }
                _uiState.value = UiState.Success(profile)
            } catch (error: Exception) {
                _uiState.value = UiState.Error(error.message ?: "Gagal memuat profil")
            }
        }
    }

    fun saveProfile(profile: Profile) {
        scope.launch {
            _operationState.value = OperationState.Loading
            try {
                repository.saveProfile(profile)
                _uiState.value = UiState.Success(profile)
                _operationState.value = OperationState.Success("Profil berhasil disimpan")
            } catch (error: Exception) {
                _operationState.value = OperationState.Error(error.message ?: "Gagal menyimpan profil")
            }
        }
    }

    fun clearOperationState() {
        _operationState.value = OperationState.Idle
    }

    fun dispose() {
        scope.cancel()
    }

    private fun defaultProfile(): Profile {
        return Profile(
            name = "Nashrullah",
            email = "nashrullah@itera.ac.id"
        )
    }
}
