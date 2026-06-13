package com.example.sholatyuk.presentation.screens.doa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sholatyuk.domain.model.Doa
import com.example.sholatyuk.domain.model.DoaCategory // Ini tambahan import penting
import com.example.sholatyuk.domain.repository.DoaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DoaViewModel(
    private val repository: DoaRepository
) : ViewModel() {

    // 1. PERBAIKAN: Ambil daftar kategori otomatis dari Enum "DoaCategory"
    // (Jika terjadi error merah pada kata "entries", ganti kata "entries" menjadi "values()")
    val categories = listOf("Semua") + DoaCategory.entries.map { it.displayName }

    // State untuk Search Bar
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // State untuk Kategori yang dipilih
    private val _selectedCategory = MutableStateFlow(categories[0]) // Default: "Semua"
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // State untuk menampung semua doa dari Database secara mentah
    private val _allDoasList = MutableStateFlow<List<Doa>>(emptyList())

    // State untuk daftar doa yang sudah difilter (yang akan dibaca oleh DoaScreen)
    private val _filteredDoaList = MutableStateFlow<List<Doa>>(emptyList())
    val filteredDoaList: StateFlow<List<Doa>> = _filteredDoaList.asStateFlow()

    init {
        // 1. Download dari API ke database lokal jika baru pertama kali install
        viewModelScope.launch {
            repository.syncDoaFromApi()
        }

        // 2. Baca terus menerus dari database lokal
        observeDoaFromDatabase()
    }

    private fun observeDoaFromDatabase() {
        viewModelScope.launch {
            repository.getAllDoa()
                .catch { e -> e.printStackTrace() }
                .collect { doas ->
                    _allDoasList.value = doas
                    applyFilter() // Langsung terapkan filter begitu data masuk
                }
        }
    }

    // Dipanggil saat pengguna mengetik di kolom pencarian
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    // Dipanggil saat pengguna mengeklik chip kategori
    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        applyFilter()
    }

    // Logika untuk menyaring doa berdasarkan teks pencarian dan kategori
    private fun applyFilter() {
        var currentList = _allDoasList.value

        // 2. PERBAIKAN: Gunakan .displayName dari Enum untuk dicocokkan dengan huruf kecil
        val category = _selectedCategory.value
        if (category != "Semua") {
            val categoryLower = category.lowercase()
            currentList = currentList.filter {
                // Sekarang .lowercase() dipanggil pada String 'displayName', bukan pada Enum-nya
                it.category.displayName.lowercase() == categoryLower
            }
        }

        // 3. Filter Pencarian Teks
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            val queryLower = query.lowercase()
            currentList = currentList.filter { doa ->
                doa.title.lowercase().contains(queryLower) ||
                        doa.translation.lowercase().contains(queryLower)
            }
        }

        // Perbarui list yang tampil di UI
        _filteredDoaList.value = currentList
    }
}