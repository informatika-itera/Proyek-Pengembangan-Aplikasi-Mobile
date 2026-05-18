package com.example.Roomie.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.data.remote.SupabaseService
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class ReportFormState(
    val category: String = "",
    val location: String = "",
    val description: String = "",
    val urgency: UrgencyLevel = UrgencyLevel.LOW,
    val selectedImage: ByteArray? = null,
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null
) {
    val isSubmitEnabled: Boolean get() = category.isNotBlank() && 
            location.isNotBlank() && 
            description.isNotBlank() && 
            !isLoading
}

class ReportViewModel(
    private val reportRepository: ReportRepository,
    private val supabaseService: SupabaseService
) : ViewModel() {
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

    fun onImagePicked(bytes: ByteArray?) {
        _state.update { it.copy(selectedImage = bytes) }
    }

    fun resetState() {
        _state.value = ReportFormState()
    }

    fun submitReport() {
        if (!_state.value.isSubmitEnabled) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                var finalImageUrl: String? = null
                
                // Real Logic Upload ke Supabase
                _state.value.selectedImage?.let { bytes ->
                    finalImageUrl = supabaseService.uploadReportImage(bytes)
                    if (finalImageUrl == null) {
                        throw Exception("Gagal mengupload gambar ke server")
                    }
                }

                val newReport = Report(
                    id = Clock.System.now().toEpochMilliseconds().toString(),
                    category = _state.value.category,
                    location = _state.value.location,
                    description = _state.value.description,
                    urgency = _state.value.urgency,
                    status = ReportStatus.PENDING,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                    imageUrl = finalImageUrl
                )
                
                reportRepository.submitReport(newReport)
                _state.update { it.copy(isLoading = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Gagal mengirim laporan") }
            }
        }
    }
}
