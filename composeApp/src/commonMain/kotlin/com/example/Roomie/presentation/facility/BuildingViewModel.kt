package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import com.example.Roomie.domain.model.Building
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BuildingViewModel : ViewModel() {
    private val _buildings = MutableStateFlow(
        listOf(
            Building("GKU1", "Gedung Kuliah Umum 1", "Gedung Kuliah Umum 1", false),
            Building("GKU2", "Gedung Kuliah Umum 2", "Gedung kuliah Umum 2", true),
            Building("GEDUNG-E", "Gedung E", "Gedung kuliah E", false),
            Building("GEDUNG-F", "Gedung F", "Gedung kuliah F", false)
        )
    )
    val buildings: StateFlow<List<Building>> = _buildings.asStateFlow()
}
