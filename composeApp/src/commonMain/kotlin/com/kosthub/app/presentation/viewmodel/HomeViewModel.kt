package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.domain.model.Kost
import com.kosthub.app.domain.repository.KostRepository
import com.kosthub.app.presentation.state.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: KostRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedDaerah = MutableStateFlow<String?>(null)
    val selectedDaerah: StateFlow<String?> = _selectedDaerah

    private val _selectedTipeKos = MutableStateFlow<String?>(null)
    val selectedTipeKos: StateFlow<String?> = _selectedTipeKos

    private val debouncedSearchQuery = _searchQuery
        .debounce(300L)
        .distinctUntilChanged()

    val uiState: StateFlow<UiState<List<Kost>>> = combine(
        repository.getAllFlow(),
        debouncedSearchQuery,
        _selectedDaerah,
        _selectedTipeKos
    ) { data, query, daerah, tipeKos ->
        val filtered = data.filter { kost ->
            val matchesQuery = query.isEmpty() || 
                kost.namaKos.contains(query, ignoreCase = true) ||
                kost.daerah.contains(query, ignoreCase = true)
            val matchesDaerah = daerah.isNullOrEmpty() || 
                kost.daerah.equals(daerah, ignoreCase = true)
            val matchesTipeKos = tipeKos.isNullOrEmpty() || 
                kost.tipeKos.equals(tipeKos, ignoreCase = true)
            
            matchesQuery && matchesDaerah && matchesTipeKos
        }
        if (filtered.isEmpty()) {
            UiState.Empty
        } else {
            UiState.Success(filtered)
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onDaerahChange(daerah: String?) {
        _selectedDaerah.value = daerah
    }

    fun onTipeKosChange(tipeKos: String?) {
        _selectedTipeKos.value = tipeKos
    }

    fun refresh() {
        scope.launch {
            try {
                repository.syncRemote()
            } catch (e: Exception) {
                // Ignore sync errors
            }
        }
    }

    fun dispose() {
        scope.cancel()
    }
}
