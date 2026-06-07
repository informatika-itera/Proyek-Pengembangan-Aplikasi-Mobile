package com.kelazzz.app.presentation.screens.jadwal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.model.JenisJadwal
import com.kelazzz.app.domain.repository.JadwalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JadwalListViewModel(
    private val repository: JadwalRepository
) : ViewModel() {

    private val _rawJadwalList = MutableStateFlow<List<Jadwal>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedJenis = MutableStateFlow<JenisJadwal?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(JadwalListUiState())
    val uiState: StateFlow<JadwalListUiState> = _uiState.asStateFlow()

    init {
        // Gabungkan list mentah dengan filter pencarian dan jenis secara reaktif
        viewModelScope.launch {
            combine(
                _rawJadwalList,
                _searchQuery,
                _selectedJenis,
                _isLoading,
                _error
            ) { list, query, jenis, loading, err ->
                val filteredList = list.filter { jadwal ->
                    val matchesSearch = jadwal.judul.contains(query, ignoreCase = true) ||
                            jadwal.deskripsi.contains(query, ignoreCase = true)
                    val matchesJenis = jenis == null || jadwal.jenis == jenis
                    matchesSearch && matchesJenis
                }
                JadwalListUiState(
                    isLoading = loading,
                    jadwalList = filteredList,
                    searchQuery = query,
                    selectedJenis = jenis,
                    error = err
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
        loadJadwal()
    }

    private var loadJob: kotlinx.coroutines.Job? = null

    fun loadJadwal() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            repository.getAllJadwal()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { list ->
                    _rawJadwalList.value = list
                    _error.value = null
                    _isLoading.value = false
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onJenisFilterChange(jenis: JenisJadwal?) {
        _selectedJenis.value = jenis
    }
}

data class JadwalListUiState(
    val isLoading: Boolean = true,
    val jadwalList: List<Jadwal> = emptyList(),
    val searchQuery: String = "",
    val selectedJenis: JenisJadwal? = null,
    val error: String? = null
)
