package com.studyhub.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.util.NetworkMonitor
import kotlinx.coroutines.flow.*

class NetworkViewModel(
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    val isOnline: StateFlow<Boolean> =
        networkMonitor.isOnline
            .catch { emit(true) }         // never crash
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = true        // start as online
            )
}
