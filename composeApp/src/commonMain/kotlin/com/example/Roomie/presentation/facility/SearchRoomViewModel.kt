package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.repository.FacilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SearchRoomViewModel(
    private val facilityRepository: FacilityRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<Room>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) kotlinx.coroutines.flow.flowOf(emptyList())
            else facilityRepository.searchRooms(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }
}
