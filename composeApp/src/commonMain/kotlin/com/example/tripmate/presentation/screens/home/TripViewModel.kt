package com.example.tripmate.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripmate.domain.model.Trip
import com.example.tripmate.domain.repository.TripRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class TripViewModel(
    private val repository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            combine(
                repository.getAllTrips(),
                _searchQuery.debounce(300)
            ) { trips, query ->
                if (query.isBlank()) {
                    trips
                } else {
                    trips.filter {
                        it.destination.contains(query, ignoreCase = true)
                    }
                }
            }
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Terjadi kesalahan")
                }
                .collect { trips ->
                    _uiState.value = if (trips.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(trips)
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun insertTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                repository.insertTrip(trip)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Gagal menyimpan trip")
            }
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                repository.updateTrip(trip)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Gagal mengupdate trip")
            }
        }
    }

    fun deleteTrip(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteTrip(id)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Gagal menghapus trip")
            }
        }
    }

    fun retry() {
        _uiState.value = HomeUiState.Loading
        loadTrips()
    }

}
