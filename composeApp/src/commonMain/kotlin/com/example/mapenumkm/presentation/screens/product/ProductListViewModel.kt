package com.example.mapenumkm.presentation.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<NoteCategory?>(null)

    val state: StateFlow<ProductListState> = combine(
        repository.getAllNotes(),
        _searchQuery,
        _selectedCategory
    ) { notes, query, category ->
        ProductListState(
            products = notes.filter { note ->
                val matchesQuery = if (query.isEmpty()) {
                    true
                } else {
                    note.title.contains(query, ignoreCase = true)
                }
                val matchesCategory = if (category == null) {
                    true
                } else {
                    note.category == category
                }
                matchesQuery && matchesCategory
            },
            searchQuery = query,
            selectedCategory = category
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductListState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryChange(category: NoteCategory?) {
        _selectedCategory.value = category
    }

    fun deleteProduct(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note.id!!)
        }
    }
}
