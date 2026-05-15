package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.usecase.GetBuildingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BuildingViewModel(
    private val getBuildingsUseCase: GetBuildingsUseCase
) : ViewModel() {
    val buildings: StateFlow<List<Building>> = getBuildingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
