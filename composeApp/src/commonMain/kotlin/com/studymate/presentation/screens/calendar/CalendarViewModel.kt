package com.studymate.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.domain.model.Reminder
import com.studymate.domain.repository.CalendarEvent
import com.studymate.domain.repository.CalendarRepository
import com.studymate.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

data class CalendarState(
    val events: List<CalendarEvent> = emptyList(),
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarState())
    val uiState = _uiState.asStateFlow()

    val reminders: StateFlow<List<Reminder>> = reminderRepository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            calendarRepository.getEvents()
                .onSuccess { events ->
                    _uiState.update { it.copy(events = events, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun addEvent(title: String, description: String?, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            val event = CalendarEvent(
                title = title,
                description = description,
                startTime = startTime,
                endTime = endTime
            )
            calendarRepository.addEvent(event).onSuccess {
                loadEvents()
            }
        }
    }

    fun addReminder(title: String, description: String?, dueDate: Long) {
        viewModelScope.launch {
            val reminder = Reminder(
                title = title,
                description = description,
                dueDate = dueDate,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
            reminderRepository.insertReminder(reminder)
        }
    }
}
