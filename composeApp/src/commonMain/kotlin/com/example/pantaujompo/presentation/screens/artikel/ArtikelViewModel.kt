package com.example.pantaujompo.presentation.screens.artikel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Serializable
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticleDto>
)

@Serializable
data class NewsArticleDto(
    val source: SourceDto,
    val title: String?,
    val description: String?, // 🔥 Tambah deskripsi buat dibaca bray
    val content: String?,     // 🔥 Tambah isi artikel
    val urlToImage: String?,
    val url: String?,
    val publishedAt: String?
)

@Serializable
data class SourceDto(
    val name: String?
)

class ArtikelViewModel(private val httpClient: HttpClient) : ViewModel() {

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
                val response: NewsResponse = httpClient.get("https://newsapi.org/v2/everything") {
                    parameter("q", "kesehatan OR olahraga")
                    parameter("language", "id")
                    parameter("sortBy", "publishedAt")
                    parameter("apiKey", "b73cac97bf0b4e1dbaef1a365a46acea")
                }.body()

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