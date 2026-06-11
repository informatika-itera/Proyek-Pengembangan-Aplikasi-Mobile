package com.example.pantaujompo.presentation.screens.artikel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.data.remote.api.NewsService
import com.example.pantaujompo.data.remote.api.NewsResponse
import com.example.pantaujompo.data.remote.api.NewsArticleDto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArtikelViewModel(private val newsService: NewsService) : ViewModel() {

    // Menyimpan data asli dari internet (Kesehatan + Olahraga)
    private val _allArticles = MutableStateFlow<List<NewsArticleDto>>(emptyList())

    // State query ketikan user
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 🔥 JURUS INSTANT FILTER: Menggabungkan data asli dengan kata kunci ketikan bray!
    val artikelList: StateFlow<List<NewsArticleDto>> = combine(_allArticles, _searchQuery) { articles, query ->
        if (query.isBlank()) {
            articles // Kalau kosong, tampilin semua berita kesehatan & olahraga terbaru
        } else {
            articles.filter {
                it.title?.contains(query, ignoreCase = true) == true ||
                        it.description?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        muatUlangBerita()
    }

    // Fungsi dipanggil pas lo hapus pencarian / klik reset
    fun muatUlangBerita() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 🔥 KITA GABUNG: Kesehatan OR Olahraga biar langsung dapet dua-duanya bray!
                val response: NewsResponse = newsService.fetchNews("kesehatan OR olahraga")

                _allArticles.value = response.articles.filter {
                    !it.title.isNullOrBlank() && !it.urlToImage.isNullOrBlank()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi dipanggil tiap lo ngetik di search bar
    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }
}