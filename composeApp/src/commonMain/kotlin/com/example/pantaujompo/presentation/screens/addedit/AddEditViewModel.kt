package com.example.pantaujompo.presentation.screens.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.domain.repository.ActivityRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class AddEditViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    fun saveActivity(
        type: String, duration: Long, distance: Double, calories: Long, notes: String, onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            repository.insertActivity(
                type = type, duration = duration, distance = distance, calories = calories, date = currentTime, notes = notes
            )
            onSuccess()
        }
    }
}