package com.example.raillog.presentation.screens.admin_main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.domain.model.Priority
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AdminMainViewModel(
    private val supplyRepository: SupplyRepository,
    userPreferences: UserPreferences
) : ViewModel() {

    private val activeUsername = userPreferences.activeUsername
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // 1. Ambil semua data dari database
    val allItems: StateFlow<List<SupplyItem>> = activeUsername.flatMapLatest { username ->
        supplyRepository.getAllItems(username)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. Filter data PENDING saja
    val pendingRequisitions: StateFlow<List<SupplyItem>> = allItems.map { items ->
        items.filter { it.status.name == "PENDING" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 3. Metrik Operasional untuk Dashboard
    val criticalPendingCount: StateFlow<Int> = pendingRequisitions.map { items ->
        items.count { it.priority == Priority.CRITICAL || it.priority == Priority.HIGH }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val averageAiConfidence: StateFlow<Int> = allItems.map { items ->
        if (items.isEmpty()) 0
        else items.sumOf { (75 + (it.id % 25)).toInt() } / items.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // State untuk Pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(0)
    val selectedFilter: StateFlow<Int> = _selectedFilter.asStateFlow()

    // FIX: filteredPendingItems di-derive dari pendingRequisitions (bukan allItems),
    // sehingga item VERIFIED/REJECTED tidak pernah muncul kembali di antrean.
    val filteredPendingItems: StateFlow<List<SupplyItem>> = combine(
        pendingRequisitions,
        _searchQuery.debounce(300L)
    ) { items, query ->
        if (query.isEmpty()) {
            items
        } else {
            items.filter { item ->
                item.name.contains(query, ignoreCase = true) ||
                        item.partCode.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fungsi untuk diakses oleh UI
    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateSelectedFilter(index: Int) { _selectedFilter.value = index }
}
