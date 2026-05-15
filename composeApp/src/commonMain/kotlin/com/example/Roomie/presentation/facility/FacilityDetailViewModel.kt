package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.usecase.GetRoomByIdUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FacilityDetailViewModel(
    private val getRoomByIdUseCase: GetRoomByIdUseCase
) : ViewModel() {
    
    fun getRoom(roomId: String): StateFlow<Room?> = getRoomByIdUseCase(roomId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
