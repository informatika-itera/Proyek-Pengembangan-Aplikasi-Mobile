package com.example.sholatyuk.presentation.screens.doa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Sudah di-import dengan benar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Model data sederhana untuk Doa
data class Doa(
    val title: String,
    val arabic: String,
    val translation: String,
    val category: String
)

class DoaViewModel : ViewModel() {

    // Data dummy Doa
    private val allDoa = listOf(
        Doa("Doa Sebelum Tidur", "بِسْمِكَ اللَّهُمَّ أَحْيَا وَأَمُوتُ", "Dengan nama-Mu, ya Allah, aku hidup dan aku mati.", "Harian"),
        Doa("Doa Bangun Tidur", "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ", "Segala puji bagi Allah yang menghidupkan kami kembali...", "Harian"),
        Doa("Doa Masuk Masjid", "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ", "Ya Allah, bukakanlah untukku pintu-pintu rahmat-Mu.", "Masjid"),
        Doa("Doa Keluar Masjid", "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ", "Ya Allah, sesungguhnya aku memohon karunia-Mu.", "Masjid"),
        Doa("Doa Sapu Jagat", "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ", "Ya Tuhan kami, berilah kami kebaikan di dunia dan akhirat...", "Mustajab")
    )

    // State untuk Search Bar
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // State untuk Filter Kategori
    val categories = listOf("Semua", "Harian", "Masjid", "Mustajab")
    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory

    // Logika Pintar: Menggabungkan Search dan Filter secara otomatis!
    val filteredDoaList = combine(_searchQuery, _selectedCategory) { query, category ->
        allDoa.filter { doa ->
            val matchesSearch = doa.title.contains(query, ignoreCase = true) ||
                    doa.translation.contains(query, ignoreCase = true)
            val matchesCategory = if (category == "Semua") true else doa.category == category

            matchesSearch && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope, // Sekarang menggunakan referensi yang benar
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = allDoa
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}