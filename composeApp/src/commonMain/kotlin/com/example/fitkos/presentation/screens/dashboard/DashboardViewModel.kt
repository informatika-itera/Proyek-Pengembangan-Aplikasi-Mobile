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
        val dateString = getTodayDateString()

        viewModelScope.launch {
            userPreferences.resetExerciseIfNewDay(dateString)

            combine(
                repository.getAllNotes(),
                waterRepository.getWaterLogByDate(dateString),
                userPreferences.userName,
                userPreferences.waterTarget,
                userPreferences.exerciseMinutesToday
            ) { notes, waterLog, userName, waterTarget, exerciseMinutes ->
                DashboardUiState(
                    userName = userName,
                    mealCount = notes.size,
                    waterGlasses = waterLog?.amount ?: 0,
                    waterTarget = waterTarget,
                    exerciseMinutes = exerciseMinutes,
                    exerciseTarget = 30,
                    mealTarget = 3
                )
            }.catch {
                _uiState.value = DashboardUiState()
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun getTodayDateString(): String {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return "${today.year}-${today.monthNumber.toString().padStart(2, '0')}-${today.dayOfMonth.toString().padStart(2, '0')}"
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