package com.example.mapenumkm.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapenumkm.data.local.datastore.UserPreferences
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.repository.TransactionRepository
import com.example.mapenumkm.domain.usecase.DeleteNoteUseCase
import com.example.mapenumkm.domain.usecase.GetAllNotesUseCase
import com.example.mapenumkm.domain.usecase.NoteSortBy
import com.example.mapenumkm.domain.usecase.SearchNotesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val repository: NoteRepository,
    private val transactionRepository: TransactionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()
    
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<NoteCategory?>(null)
    private val _sortBy = MutableStateFlow(NoteSortBy.UPDATED_DESC)
    private val _isLoading = MutableStateFlow(false)
    
    private val debouncedSearchQuery = _searchQuery.debounce(300)
    
    val sortBy: StateFlow<NoteSortBy> = _sortBy
    
    val uiState: StateFlow<HomeUiState> = combine(
        debouncedSearchQuery,
        _selectedCategory,
        _sortBy,
        transactionRepository.getAllTransactions()
    ) { query, category, sortBy, transactions ->
        val notesFlow = if (query.isBlank() && category == null) {
            getAllNotesUseCase(sortBy)
        } else {
            searchNotesUseCase(query, category, sortBy)
        }
        
        // Calculate sold counts from transactions
        val soldCounts = mutableMapOf<Long, Int>()
        transactions.forEach { transaction ->
            transaction.items.forEach { item ->
                soldCounts[item.productId] = (soldCounts[item.productId] ?: 0) + item.quantity
            }
        }
        
        notesFlow.map { notes ->
            Pair(notes, soldCounts)
        }
    }.flatMapLatest { it }
    .combine(_isLoading) { (notes, soldCounts), isLoading ->
        when {
            isLoading -> HomeUiState.Loading
            notes.isEmpty() -> HomeUiState.Empty(
                query = _searchQuery.value,
                category = _selectedCategory.value
            )
            else -> HomeUiState.Success(
                notes = notes,
                soldCounts = soldCounts,
                totalProducts = notes.size,
                totalStockValue = notes.sumOf { it.price * it.stock },
                lowStockCount = notes.count { it.stock <= 5 },
                query = _searchQuery.value,
                category = _selectedCategory.value,
                sortBy = _sortBy.value
            )
        }
    }.catch { e ->
        emit(HomeUiState.Error(e.message ?: "Terjadi kesalahan"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )
    
    // ==================== USER ACTIONS ====================
    
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    fun onCategorySelected(category: NoteCategory?) {
        _selectedCategory.value = category
    }
    
    fun onSortByChanged(sortBy: NoteSortBy) {
        _sortBy.value = sortBy
    }
    
    fun togglePin(noteId: Long) {
        viewModelScope.launch {
            repository.togglePinNote(noteId)
        }
    }
    
    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
        }
    }
    
    fun deleteNotes(noteIds: List<Long>) {
        viewModelScope.launch {
            repository.deleteNotes(noteIds)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.setLoggedIn(false)
            _events.emit(HomeEvent.LoggedOut)
        }
    }
}

sealed interface HomeEvent {
    data object LoggedOut : HomeEvent
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    
    data class Success(
        val notes: List<Note>,
        val soldCounts: Map<Long, Int> = emptyMap(),
        val totalProducts: Int = 0,
        val totalStockValue: Double = 0.0,
        val lowStockCount: Int = 0,
        val query: String = "",
        val category: NoteCategory? = null,
        val sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC
    ) : HomeUiState
    
    data class Empty(
        val query: String = "",
        val category: NoteCategory? = null
    ) : HomeUiState
    
    data class Error(val message: String) : HomeUiState
}
