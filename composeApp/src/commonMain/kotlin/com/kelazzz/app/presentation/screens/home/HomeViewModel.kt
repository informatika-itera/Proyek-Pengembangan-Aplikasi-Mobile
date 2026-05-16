package com.kelazzz.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelazzz.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val userName: StateFlow<String> = authRepository.currentUser
        .map { it?.nama ?: "Mahasiswa" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Mahasiswa")

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _events.emit(HomeEvent.LoggedOut)
        }
    }
}

sealed interface HomeEvent {
    data object LoggedOut : HomeEvent
}
