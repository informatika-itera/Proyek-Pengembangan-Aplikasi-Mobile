package com.example.bookku.presentation.screens.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookku.domain.model.*
import com.example.bookku.domain.repository.*
import com.example.bookku.domain.usecase.deleteBookUseCase
import com.example.bookku.domain.usecase.GetRecommendationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class BookDetailViewModel(
    private val repository: NoteRepository,
    private val deleteBookUseCase: deleteBookUseCase,
    private val reviewRepository: ReviewRepository,
    private val progressRepository: ReadingProgressRepository,
    private val authRepository: AuthRepository,
    private val aiRepository: AIRepository,
    private val getRecommendationUseCase: GetRecommendationUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<NoteDetailUiState>(NoteDetailUiState.Loading)
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<NoteDetailEvent>()
    val events: SharedFlow<NoteDetailEvent> = _events.asSharedFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _ratingStats = MutableStateFlow(RatingStats())
    val ratingStats: StateFlow<RatingStats> = _ratingStats.asStateFlow()

    private val _readingProgress = MutableStateFlow<ReadingProgress?>(null)
    val readingProgress: StateFlow<ReadingProgress?> = _readingProgress.asStateFlow()

    private val _aiRecommendation = MutableStateFlow<String?>(null)
    val aiRecommendation: StateFlow<String?> = _aiRecommendation.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private var currentNoteId: Long? = null
    private var extraDataJob: Job? = null
    
    fun loadNote(noteId: Long) {
        if (currentNoteId == noteId) return
        currentNoteId = noteId
        
        _aiRecommendation.value = null
        extraDataJob?.cancel()
        extraDataJob = null
        
        viewModelScope.launch {
            // Ambil ID user saat ini untuk pengecekan kepemilikan
            val currentUserId = authRepository.getCurrentUserId() ?: ""
            
            repository.getBookById(noteId).collect { book ->
                if (book != null) {
                    // Cek apakah user adalah pemilik buku (owner)
                    val isOwner = book.userId.isNotBlank() && book.userId == currentUserId
                    _uiState.value = NoteDetailUiState.Success(book, isOwner)
                    
                    if (extraDataJob == null) {
                        loadExtraData(book, currentUserId)
                    }
                } else {
                    _uiState.value = NoteDetailUiState.NotFound
                }
            }
        }
    }

    private fun loadExtraData(book: Book, userId: String) {
        extraDataJob?.cancel()
        extraDataJob = viewModelScope.launch {
            launch { reviewRepository.getReviewsByBook(book.id).collect { _reviews.value = it } }
            launch { reviewRepository.getRatingStats(book.id).collect { _ratingStats.value = it } }
            launch { 
                if (userId.isNotBlank()) {
                    progressRepository.getReadingProgress(userId, book.id).collect { _readingProgress.value = it }
                }
            }
            launch { repository.getCachedRecommendation(book.id).collect { _aiRecommendation.value = it } }
        }
    }
    
    fun togglePin() {
        val state = uiState.value
        if (state is NoteDetailUiState.Success) {
            viewModelScope.launch {
                repository.togglePinNote(state.book.id)
            }
        }
    }
    
    fun deleteBook() {
        val state = uiState.value as? NoteDetailUiState.Success ?: return
        if (!state.isOwner) {
            viewModelScope.launch { _events.emit(NoteDetailEvent.Error("Hanya pemilik yang bisa menghapus")) }
            return
        }

        viewModelScope.launch {
            deleteBookUseCase(state.book.id)
                .onSuccess { _events.emit(NoteDetailEvent.NoteDeleted) }
                .onFailure { error -> _events.emit(NoteDetailEvent.Error(error.message ?: "Gagal menghapus")) }
        }
    }

    fun addReview(rating: Float, text: String) {
        val bookId = currentNoteId ?: return
        viewModelScope.launch {
            val user = authRepository.currentUser.first() ?: return@launch
            val review = Review(
                bookId = bookId,
                userId = user.id,
                userName = user.username,
                rating = rating,
                reviewText = text,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
            reviewRepository.addOrUpdateReview(review)
        }
    }

    fun updateProgress(currentPage: Int) {
        val bookId = currentNoteId ?: return
        val state = uiState.value as? NoteDetailUiState.Success ?: return
        val totalPages = state.book.totalPages
        
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val current = _readingProgress.value
            if (current == null) {
                progressRepository.addReadingProgress(
                    userId = userId,
                    progress = ReadingProgress(
                        userId = userId,
                        bookId = bookId,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        status = if (currentPage >= totalPages && totalPages > 0) ReadingStatus.COMPLETED else ReadingStatus.READING,
                        startedAt = Clock.System.now()
                    )
                )
            } else {
                progressRepository.updateProgress(
                    userId = userId,
                    bookId = bookId,
                    currentPage = currentPage,
                    status = if (currentPage >= totalPages && totalPages > 0) ReadingStatus.COMPLETED else ReadingStatus.READING
                )
            }
        }
    }

    fun fetchAiRecommendation() {
        val book = (uiState.value as? NoteDetailUiState.Success)?.book ?: return
        
        // Menghapus validasi content.isBlank() agar AI tetap bisa menganalisis 
        // berdasarkan Judul dan Penulis jika deskripsi kosong.

        _isAiLoading.value = true
        viewModelScope.launch {
            getRecommendationUseCase(book)
                .collect { result ->
                    result.onSuccess { recommendation ->
                        _aiRecommendation.value = recommendation
                    }.onFailure { error ->
                        _events.emit(NoteDetailEvent.Error("AI Error: ${error.message}"))
                    }
                    _isAiLoading.value = false
                }
        }
    }
}

sealed interface NoteDetailUiState {
    data object Loading : NoteDetailUiState
    data class Success(val book: Book, val isOwner: Boolean) : NoteDetailUiState
    data object NotFound : NoteDetailUiState
}

sealed interface NoteDetailEvent {
    data object NoteDeleted : NoteDetailEvent
    data class Error(val message: String) : NoteDetailEvent
}
