package com.example.arcane.presentation.screens.letterbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.ReadingStatus
import com.example.arcane.domain.repository.AIRepository
import com.example.arcane.domain.repository.BookRepository
import com.example.arcane.domain.repository.FolderRepository
import com.example.arcane.domain.model.Folder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@kotlinx.serialization.Serializable
data class AIRecommendationBook(
    val title: String,
    val author: String,
    val coverUrl: String,
    val genres: List<String>,
    val shortDescription: String
)

class LetterboxViewModel(
    private val repository: BookRepository,
    private val aiRepository: AIRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {

    val uiState: StateFlow<LetterboxUiState> = repository
        .getBooksByStatus(ReadingStatus.COMPLETED)
        .map { books ->
            if (books.isEmpty()) LetterboxUiState.Empty
            else LetterboxUiState.Success(books)
        }
        .catch { e -> emit(LetterboxUiState.Error(e.message ?: "Terjadi kesalahan")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LetterboxUiState.Loading
        )

    val folders: StateFlow<List<Folder>> = folderRepository
        .getAllFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _reviewStates = MutableStateFlow<Map<String, ReviewState>>(emptyMap())
    val reviewStates: StateFlow<Map<String, ReviewState>> = _reviewStates.asStateFlow()

    private val _recommendationState = MutableStateFlow<RecommendationState>(cachedRecommendationState)
    val recommendationState: StateFlow<RecommendationState> = _recommendationState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refreshLetterboxData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            kotlinx.coroutines.delay(800)
            _isRefreshing.value = false

        }
    }

    fun createFolder(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            folderRepository.createFolder(name.trim())
        }
    }

    fun deleteFolder(id: Long) {
        viewModelScope.launch {
            folderRepository.deleteFolder(id)
        }
    }

    fun updateFolder(id: Long, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            folderRepository.updateFolderName(id, newName.trim())
        }
    }

    fun generateReview(book: Book) {
        viewModelScope.launch {
            _reviewStates.update { current ->
                current + (book.googleBookId to ReviewState.Loading)
            }
            val result = aiRepository.chat(
                bookTitle = book.title,
                bookDescription = book.description,
                question = "Buatkan ulasan singkat dan menarik untuk buku '${book.title}' karya ${book.authorsFormatted} dalam Bahasa Indonesia. Maksimal 3 paragraf."
            )
            result
                .onSuccess { review ->
                    _reviewStates.update { current ->
                        current + (book.googleBookId to ReviewState.Success(review))
                    }
                }
                .onFailure { error ->
                    _reviewStates.update { current ->
                        current + (book.googleBookId to ReviewState.Error(error.message ?: "Gagal generate review"))
                    }
                }
        }
    }

    fun getRecommendations(books: List<Book>) {
        viewModelScope.launch {
            updateRecommendationState(RecommendationState.Loading)
            val bookTitles = books.joinToString(", ") { it.title }

            val jsonPrompt = """
                Berdasarkan buku-buku berikut yang sudah aku baca: $bookTitles.
                Rekomendasikan 5 buku fiksi atau literatur lain yang memiliki keterikatan tema serupa.
                Kembalikan hasil dalam bentuk JSON Array murni tanpa kata pembuka, tanpa kata penutup, dan tanpa bungkus markdown seperti ```json ```. 
                Struktur objek JSON harus memiliki field persis seperti ini:
                [
                  {
                    "title": "Judul Buku",
                    "author": "Nama Penulis",
                    "coverUrl": "URL Gambar Sampul",
                    "genres": ["Genre1", "Genre2"],
                    "shortDescription": "Alasan singkat rekomendasi dalam Bahasa Indonesia"
                  }
                ]
            """.trimIndent()

            val result = aiRepository.chat(
                bookTitle = "Rekomendasi Buku",
                bookDescription = "Buku yang sudah dibaca: $bookTitles",
                question = jsonPrompt
            )

            result
                .onSuccess { jsonString ->
                    try {
                        val cleanJson = jsonString
                            .replace("```json", "")
                            .replace("```", "")
                            .trim()

                        val parsedBooks = Json.decodeFromString<List<AIRecommendationBook>>(cleanJson)
                        updateRecommendationState(RecommendationState.Success(parsedBooks))
                    } catch (e: Exception) {
                        updateRecommendationState(RecommendationState.Error("Gagal memproses struktur data AI."))
                    }
                }
                .onFailure { error ->
                    updateRecommendationState(RecommendationState.Error(error.message ?: "Gagal mendapat rekomendasi"))
                }
        }
    }

    fun dismissRecommendation() {
        updateRecommendationState(RecommendationState.Idle)
    }

    private fun updateRecommendationState(state: RecommendationState) {
        cachedRecommendationState = state
        _recommendationState.value = state
    }

    companion object {
        private var cachedRecommendationState: RecommendationState = RecommendationState.Idle
    }
}

sealed interface ReviewState {
    data object Idle : ReviewState
    data object Loading : ReviewState
    data class Success(val review: String) : ReviewState
    data class Error(val message: String) : ReviewState
}

sealed interface RecommendationState {
    data object Idle : RecommendationState
    data object Loading : RecommendationState
    data class Success(val recommendations: List<AIRecommendationBook>) : RecommendationState
    data class Error(val message: String) : RecommendationState
}

sealed interface LetterboxUiState {
    data object Loading : LetterboxUiState
    data class Success(val books: List<Book>) : LetterboxUiState
    data object Empty : LetterboxUiState
    data class Error(val message: String) : LetterboxUiState
}