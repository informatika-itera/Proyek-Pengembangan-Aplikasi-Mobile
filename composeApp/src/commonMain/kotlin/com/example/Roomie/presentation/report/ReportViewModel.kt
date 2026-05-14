package com.example.Roomie.presentation.report

import androidx.lifecycle.ViewModel
import com.example.Roomie.domain.model.UrgencyLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReportFormState(
    val category: String = "",
    val location: String = "",
    val description: String = "",
    val urgency: UrgencyLevel = UrgencyLevel.LOW,
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false
) {
    val isSubmitEnabled: Boolean get() = category.isNotBlank() && 
            location.isNotBlank() && 
            description.isNotBlank() && 
            !isLoading
}

class ReportViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReportFormState())
    val state: StateFlow<ReportFormState> = _state.asStateFlow()

    fun onCategoryChange(category: String) {
        _state.update { it.copy(category = category) }
    }

    fun onLocationChange(location: String) {
        _state.update { it.copy(location = location) }
    }

    fun onDescriptionChange(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun onUrgencyChange(urgency: UrgencyLevel) {
        _state.update { it.copy(urgency = urgency) }
    }

    fun submitReport() {
        if (!_state.value.isSubmitEnabled) return
        
        _state.update { it.copy(isLoading = true) }
        // Simulasi pengiriman data
        _state.update { it.copy(isLoading = false, isSubmitted = true) }
    }
}
