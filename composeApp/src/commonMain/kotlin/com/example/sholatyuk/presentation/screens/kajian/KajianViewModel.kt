package com.example.sholatyuk.presentation.screens.kajian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sholatyuk.domain.model.KajianCategory
import com.example.sholatyuk.domain.model.KajianNote
import com.example.sholatyuk.domain.repository.KajianRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class KajianUiState(
    val notes: List<KajianNote> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: KajianCategory? = null,
    val isLoading: Boolean = false
)

class KajianViewModel(
    private val repository: KajianRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<KajianCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    val uiState: StateFlow<KajianUiState> = combine(
        repository.getAllNotes(),
        _searchQuery,
        _selectedCategory
    ) { notes, query, category ->
        val filteredNotes = notes.filter { note ->
            val matchesQuery = note.judul.contains(query, ignoreCase = true) || 
                               note.ustadz.contains(query, ignoreCase = true)
            val matchesCategory = category == null || note.kategori == category
            matchesQuery && matchesCategory
        }
        KajianUiState(
            notes = filteredNotes,
            searchQuery = query,
            selectedCategory = category
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = KajianUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: KajianCategory?) {
        _selectedCategory.value = category
    }

    fun addNote(judul: String, ustadz: String, tanggal: String, kategori: KajianCategory, isi: String) {
        viewModelScope.launch {
            val note = KajianNote(
                id = Random.nextLong().toString(),
                judul = judul,
                ustadz = ustadz,
                tanggal = tanggal,
                kategori = kategori,
                isi = isi
            )
            repository.insertNote(note)
        }
    }

    fun updateNote(note: KajianNote) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }
}