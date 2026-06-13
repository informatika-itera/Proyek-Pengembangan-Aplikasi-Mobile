package com.example.raillog.presentation.screens.staff_main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.model.SupplyStatus
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class StaffMainViewModel(
    private val supplyRepository: SupplyRepository,
    val userPreferences: UserPreferences
) : ViewModel() {

    private val activeUsername = userPreferences.activeUsername
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val activeUserRole = userPreferences.userRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Staff Gudang")

    init {
        viewModelScope.launch {
            activeUsername.collect { username ->
                if (username.isNotEmpty()) {
                    supplyRepository.migrateDataToUser(username)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allSupplyItems = activeUsername.flatMapLatest { username ->
        supplyRepository.getAllItems(username)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val allDrafts = activeUsername.flatMapLatest { username ->
        supplyRepository.getAllDrafts(username)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ==================== SEARCH & FILTER STATE ====================

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    private val _historyFilter = MutableStateFlow("All Requests")
    val historyFilter: StateFlow<String> = _historyFilter.asStateFlow()

    private val _inventoryCategory = MutableStateFlow("All")
    val inventoryCategory: StateFlow<String> = _inventoryCategory.asStateFlow()

    // ==================== FILTERED FLOWS ====================

    // Inventory: filter by category + search
    val filteredInventoryItems: StateFlow<List<SupplyItem>> = combine(
        allSupplyItems,
        _searchQuery.debounce(300L),
        _inventoryCategory
    ) { items, query, category ->
        val approvedItems = items.filter { it.status.name == "VERIFIED" }
        approvedItems.filter { item ->
            val matchCategory = category == "All" ||
                    item.category.name.replace("_", " ").equals(category, ignoreCase = true)
            val matchSearch = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.partCode.contains(query, ignoreCase = true)
            matchCategory && matchSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // History: filter by status + search
    val filteredHistoryItems: StateFlow<List<SupplyItem>> = combine(
        allSupplyItems,
        _historySearchQuery.debounce(300L),
        _historyFilter
    ) { items, query, filter ->
        val statusFiltered = when (filter) {
            "Pending"  -> items.filter { it.status == SupplyStatus.PENDING }
            "Verified" -> items.filter { it.status == SupplyStatus.VERIFIED }
            "Rejected" -> items.filter { it.status == SupplyStatus.REJECTED }
            else       -> items
        }
        if (query.isBlank()) statusFiltered
        else statusFiltered.filter { item ->
            item.name.contains(query, ignoreCase = true) ||
                    item.partCode.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Requests: search across all items
    val filteredRequestItems: StateFlow<List<SupplyItem>> = combine(
        allSupplyItems,
        _searchQuery.debounce(300L)
    ) { items, query ->
        if (query.isBlank()) items
        else items.filter { item ->
            item.name.contains(query, ignoreCase = true) ||
                    item.partCode.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ==================== UPDATE FUNCTIONS ====================

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun updateHistoryFilter(filter: String) {
        _historyFilter.value = filter
    }

    fun updateInventoryCategory(category: String) {
        _inventoryCategory.value = category
    }
}
