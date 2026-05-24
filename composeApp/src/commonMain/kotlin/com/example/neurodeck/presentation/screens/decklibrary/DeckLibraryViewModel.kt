package com.example.neurodeck.presentation.screens.decklibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * State untuk Deck Library screen (Decks Tab).
 *
 * Sealed interface = exhaustive when di Compose, compiler memaksa handle semua case.
 * Lebih aman dari class biasa dengan `isLoading: Boolean` + `error: String?` yang
 * bisa kombinasi invalid (loading=true tapi error juga di-set).
 *
 * P3d note: Tambah varian `NoSearchResults` untuk kasus search query tidak match
 * apapun (vs Empty yang berarti DB benar-benar kosong belum ada deck sama sekali).
 */
sealed interface DeckLibraryUiState {
    data object Loading : DeckLibraryUiState

    /** DB benar-benar kosong — user belum pernah buat deck apa pun. */
    data object Empty : DeckLibraryUiState

    /** Ada deck tapi search query tidak match. Bukan empty state real. */
    data class NoSearchResults(val query: String) : DeckLibraryUiState

    data class Success(val decks: List<Deck>) : DeckLibraryUiState
    data class Error(val message: String) : DeckLibraryUiState
}

/**
 * ViewModel untuk Decks Tab.
 *
 * P3d Enhancement: combine deck flow dengan search query StateFlow supaya
 * UI auto-filter saat user mengetik. Pattern ini scalable kalau nanti ada
 * filter chips juga (All/Due/Favorite) — tinggal tambah StateFlow filter lagi.
 *
 * Cicilan Sprint 3 — Search/Filter 25% rubric:
 *   Filter logic di sini = client-side (in-memory). Untuk Sprint 2 cukup
 *   karena jumlah deck per user terbatas (puluhan, bukan ribuan).
 *   Sprint 3 bisa di-upgrade ke SQL `WHERE title LIKE '%query%'` kalau
 *   data besar.
 */
class DeckLibraryViewModel(
    private val repository: DeckRepository,
) : ViewModel() {

    // ════════════════════════════════════════════════════════════════════════
    // SEARCH QUERY — public StateFlow supaya UI bisa observe + update.
    // ════════════════════════════════════════════════════════════════════════
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ════════════════════════════════════════════════════════════════════════
    // UI STATE — derived dari combine(decks flow, search query)
    // ════════════════════════════════════════════════════════════════════════
    val uiState: StateFlow<DeckLibraryUiState> = combine(
        repository.observeAllDecks(),
        _searchQuery,
    ) { decks, query ->
        when {
            decks.isEmpty() -> DeckLibraryUiState.Empty
            query.isBlank() -> DeckLibraryUiState.Success(decks)
            else -> {
                val filtered = decks.filter { deck ->
                    deck.title.contains(query, ignoreCase = true) ||
                            deck.description.contains(query, ignoreCase = true)
                }
                if (filtered.isEmpty()) {
                    DeckLibraryUiState.NoSearchResults(query)
                } else {
                    DeckLibraryUiState.Success(filtered)
                }
            }
        }
    }
        .catch { e ->
            emit(DeckLibraryUiState.Error(e.message ?: "Gagal memuat decks"))
        }
        .stateIn(
            scope = viewModelScope,
            // WhileSubscribed(5000) — flow tetap aktif 5 detik setelah no subscriber
            // supaya bertahan saat config change (rotate device dll). Standard
            // pattern untuk StateFlow di ViewModel.
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DeckLibraryUiState.Loading,
        )

    /**
     * Update search query. Dipanggil dari SearchBar onValueChange.
     * Tidak ada debounce — filter in-memory ringan, langsung trigger.
     * Kalau nanti pindah ke DB query, tambah debounce di UI layer
     * (e.g. snapshotFlow + debounce 300ms).
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    // ════════════════════════════════════════════════════════════════════════
    // CRUD OPERATIONS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Bikin deck baru lalu return ID supaya UI bisa navigate ke deck detail.
     * onSuccess callback dipanggil dengan ID baru, supaya navigation dipicu dari UI layer
     * (ViewModel tidak boleh tahu tentang NavController).
     */
    fun createDeck(title: String, description: String = "", onSuccess: (Long) -> Unit) {
        if (title.isBlank()) return  // validation sederhana
        viewModelScope.launch {
            try {
                val newId = repository.createDeck(title.trim(), description.trim())
                onSuccess(newId)
            } catch (e: Exception) {
                // Note: error tidak emit ke uiState karena state didrive dari
                // combine flow di atas. Kalau ada CRUD error, log di console
                // saja — UI tidak akan tahu (acceptable trade-off untuk Sprint 2).
                println("createDeck failed: ${e.message}")
            }
        }
    }

    fun deleteDeck(deckId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteDeck(deckId)
            } catch (e: Exception) {
                println("deleteDeck failed: ${e.message}")
            }
        }
    }

    /**
     * Update title/description deck existing.
     * Validation: title harus non-blank (sama dengan createDeck).
     */
    fun updateDeck(deck: Deck, newTitle: String, newDescription: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            try {
                repository.updateDeck(
                    deck.copy(
                        title = newTitle.trim(),
                        description = newDescription.trim(),
                    ),
                )
            } catch (e: Exception) {
                println("updateDeck failed: ${e.message}")
            }
        }
    }
}