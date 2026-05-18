package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.repository.FacilityRepository
import com.example.Roomie.data.repository.FacilityRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BuildingViewModel(
    private val facilityRepository: FacilityRepository
) : ViewModel() {
    
    init {
        // FIX: Pastikan seeding terpanggil saat pertama kali list gedung dibuka
        (facilityRepository as? FacilityRepositoryImpl)?.let {
            viewModelScope.launch {
                it.seedData()
            }
        }
    }

    val buildings: StateFlow<List<Building>> = facilityRepository.getBuildings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
