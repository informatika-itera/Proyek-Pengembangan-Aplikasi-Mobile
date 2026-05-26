package com.example.fitkos.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitkos.data.local.datastore.UserPreferences
import com.example.fitkos.domain.repository.NoteRepository
import com.example.fitkos.domain.repository.WaterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DashboardViewModel(
    private val repository: NoteRepository,
    private val waterRepository: WaterRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateString = "${today.year}-${today.monthNumber.toString().padStart(2, '0')}-${today.dayOfMonth.toString().padStart(2, '0')}"

        viewModelScope.launch {
            combine(
                repository.getAllNotes(),
                waterRepository.getWaterLogByDate(dateString),
                userPreferences.userName,
                userPreferences.waterTarget
            ) { notes, waterLog, userName, waterTarget ->
                DashboardUiState(
                    userName = userName,
                    mealCount = notes.size,
                    waterGlasses = waterLog?.amount ?: 0,
                    waterTarget = waterTarget
                )
            }.catch { e ->
                // Handle error
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}

data class DashboardUiState(
    val userName: String = "Sobat Kos",
    val mealCount: Int = 0,
    val waterGlasses: Int = 0,
    val waterTarget: Int = 8,
    val exerciseMinutes: Int = 0,
    val exerciseTarget: Int = 30,
    val mealTarget: Int = 3
)
