package com.kelazzz.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.repository.AuthRepository
import com.kelazzz.app.domain.repository.JadwalRepository
import com.kelazzz.app.domain.repository.PresensiRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val jadwalRepository: JadwalRepository,
    private val presensiRepository: PresensiRepository
) : ViewModel() {

    val userName: StateFlow<String> = authRepository.currentUser
        .map { it?.nama ?: "Mahasiswa" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Mahasiswa")

    val upcomingJadwal: StateFlow<List<Jadwal>> = jadwalRepository.getAllJadwal()
        .map { list -> list.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceWarnings: StateFlow<List<AttendanceSummary>> =
        presensiRepository.getAttendanceSummary()
            .map { summaries ->
                summaries
                    .filter { it.totalAlpha > 1 }
                    .sortedByDescending { it.totalAlpha }
                    .take(3)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
