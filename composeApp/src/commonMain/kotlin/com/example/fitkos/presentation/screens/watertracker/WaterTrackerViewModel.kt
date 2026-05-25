package com.example.fitkos.presentation.screens.watertracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitkos.data.local.datastore.UserPreferences
import com.example.fitkos.domain.model.WaterLog
import com.example.fitkos.domain.repository.WaterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class WaterTrackerViewModel(
    private val repository: WaterRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val todayDate: String
        get() {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')}"
        }

    private val _waterLog = repository.getWaterLogByDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val uiState: StateFlow<WaterTrackerUiState> = combine(
        _waterLog,
        userPreferences.waterTarget,
        repository.getWaterHistory()
    ) { log, target, history ->
        WaterTrackerUiState(
            amount = log?.amount ?: 0,
            target = target,
            history = history
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        WaterTrackerUiState()
    )

    fun addGlass() {
        viewModelScope.launch {
            val currentLog = _waterLog.value
            val target = userPreferences.waterTarget.first()
            if (currentLog == null) {
                repository.upsertWaterLog(WaterLog(todayDate, 1, target))
            } else {
                repository.updateWaterAmount(todayDate, currentLog.amount + 1)
            }
        }
    }
    
    fun resetToday() {
        viewModelScope.launch {
             repository.updateWaterAmount(todayDate, 0)
        }
    }
}

data class WaterTrackerUiState(
    val amount: Int = 0,
    val target: Int = 8,
    val history: List<WaterLog> = emptyList()
)
