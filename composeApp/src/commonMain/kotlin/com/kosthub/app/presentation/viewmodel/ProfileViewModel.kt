package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.domain.model.Profile
import com.kosthub.app.domain.repository.ProfileRepository
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

    init {
        loadProfile()
    }

    fun loadProfile() {
        scope.launch {
            _uiState.value = UiState.Loading
            val profile = repository.getProfile() ?: defaultProfile().also {
                repository.saveProfile(it)
            }
            _uiState.value = UiState.Success(profile)
        }
    }

    fun saveProfile(profile: Profile) {
        scope.launch {
            repository.saveProfile(profile)
            _uiState.value = UiState.Success(profile)
        }
    }

    fun dispose() {
        scope.cancel()
    }

    private fun defaultProfile(): Profile {
        return Profile(
            name = "Nashrullah",
            role = "Mahasiswa ITERA",
            contact = "nashrullah@itera.ac.id",
            preference = "Harga < 1.5jt",
            location = "Dekat Kampus"
        )
    }
}
