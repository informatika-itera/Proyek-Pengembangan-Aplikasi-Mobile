package com.example.neurodeck.presentation.screens.importgenerate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.repository.AIRepository
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════════════════════
// ImportGenerateUiState — multi-phase state machine
//
// Flow phases:
//   Input        → user mengetik/paste material
//   Generating   → AI request in flight
//   Preview      → AI selesai, user lihat hasil + edit/delete cards
//   Saving       → bulk insert ke DB
//   Done         → success, callback ke navigation
//   Error        → gagal di salah satu phase, allow retry
//
// State pakai data class flat (bukan sealed interface) karena banyak field
// yang persist antar-phase (material text, deck name, dll) — sealed akan
// memaksa kita re-declare di setiap variant atau pakai nullable.
// Trade-off: caller harus cek `phase` enum manual (kurang exhaustive).
// ════════════════════════════════════════════════════════════════════════════

/**
 * Card draft yang muncul di Preview phase.
 * User bisa edit field-nya atau delete sebelum confirm save.
 *
 * @property id  Local-only ID supaya LazyColumn `key` stabil saat reorder/delete.
 *               TIDAK related dengan DB ID (kartu belum di-save). Pakai counter.
 */
data class CardDraft(
    val id: Long,
    val front: String,
    val back: String,
)

enum class GeneratePhase {
    Input,
    Generating,
    Preview,
    Saving,
    Done,
    Error,
}

data class ImportGenerateUiState(
    val phase: GeneratePhase = GeneratePhase.Input,
    val material: String = "",
    val cardCount: Int = DEFAULT_CARD_COUNT,
    val drafts: List<CardDraft> = emptyList(),
    val errorMessage: String? = null,
    /** Set saat semua selesai save — Screen pakai ini untuk trigger navigation. */
    val savedCardsCount: Int = 0,
) {
    val canGenerate: Boolean
        get() = phase == GeneratePhase.Input &&
                material.trim().length in MIN_MATERIAL_LENGTH..MAX_MATERIAL_LENGTH

    val canSave: Boolean
        get() = phase == GeneratePhase.Preview && drafts.isNotEmpty()

    companion object {
        const val MIN_MATERIAL_LENGTH = 20
        const val MAX_MATERIAL_LENGTH = 10_000
        const val MIN_CARD_COUNT = 5
        const val MAX_CARD_COUNT = 15
        const val DEFAULT_CARD_COUNT = 10
    }
}

/**
 * ViewModel untuk ImportGenerate flow.
 *
 * @param deckId  ID deck target untuk insert cards. Required.
 *                Kalau dipanggil dari Drawer (Quick Generate), caller harus
 *                bikin deck baru DULU di luar VM ini lalu pass ID-nya.
 *                Kalau pass 0L, save akan fail — VM tidak handle deck creation.
 */
class ImportGenerateViewModel(
    private val deckId: Long,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val aiRepository: AIRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportGenerateUiState())
    val uiState: StateFlow<ImportGenerateUiState> = _uiState.asStateFlow()

    // Counter untuk generate local ID untuk CardDraft.
    // Tidak perlu thread-safe karena semua state update di main dispatcher.
    private var draftIdCounter: Long = 0

    // ════════════════════════════════════════════════════════════════════════
    // PHASE: Input → user typing material
    // ════════════════════════════════════════════════════════════════════════

    fun onMaterialChange(value: String) {
        // Limit panjang supaya tidak overflow Gemini API context window
        // dan UX-nya tidak laggy (TextField sangat panjang).
        val trimmed = value.take(ImportGenerateUiState.MAX_MATERIAL_LENGTH)
        _uiState.update { it.copy(material = trimmed, errorMessage = null) }
    }

    fun onCardCountChange(value: Int) {
        val clamped = value.coerceIn(
            ImportGenerateUiState.MIN_CARD_COUNT,
            ImportGenerateUiState.MAX_CARD_COUNT,
        )
        _uiState.update { it.copy(cardCount = clamped) }
    }

    // ════════════════════════════════════════════════════════════════════════
    // PHASE: Input → Generating → Preview
    // ════════════════════════════════════════════════════════════════════════

    fun generate() {
        val current = _uiState.value
        if (!current.canGenerate) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(phase = GeneratePhase.Generating, errorMessage = null)
            }
            try {
                // NOTE: AIRepository.generateFlashcards() existing signature
                // tidak terima cardCount parameter. Untuk Sprint 2 kita pass
                // material apa adanya — AI akan generate jumlah default (~10).
                // Sprint 3+: extend AIRepository.generateFlashcards(material, count).
                val rawCards = aiRepository.generateFlashcards(current.material.trim())

                if (rawCards.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            phase = GeneratePhase.Error,
                            errorMessage = "AI tidak menghasilkan kartu. " +
                                    "Coba materi yang lebih panjang atau lebih jelas.",
                        )
                    }
                    return@launch
                }

                // Convert hasil AI ke CardDraft dengan local ID.
                val drafts = rawCards.map { (front, back) ->
                    CardDraft(
                        id = ++draftIdCounter,
                        front = front,
                        back = back,
                    )
                }

                _uiState.update {
                    it.copy(phase = GeneratePhase.Preview, drafts = drafts)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        phase = GeneratePhase.Error,
                        errorMessage = e.message ?: "Gagal generate flashcards. Coba lagi.",
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // PHASE: Preview → user edit/delete drafts
    // ════════════════════════════════════════════════════════════════════════

    fun onDraftFrontChange(draftId: Long, newFront: String) {
        _uiState.update { state ->
            state.copy(
                drafts = state.drafts.map { draft ->
                    if (draft.id == draftId) draft.copy(front = newFront) else draft
                },
            )
        }
    }

    fun onDraftBackChange(draftId: Long, newBack: String) {
        _uiState.update { state ->
            state.copy(
                drafts = state.drafts.map { draft ->
                    if (draft.id == draftId) draft.copy(back = newBack) else draft
                },
            )
        }
    }

    fun deleteDraft(draftId: Long) {
        _uiState.update { state ->
            state.copy(drafts = state.drafts.filterNot { it.id == draftId })
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // PHASE: Preview → Saving → Done
    // ════════════════════════════════════════════════════════════════════════

    fun saveAll() {
        val current = _uiState.value
        if (!current.canSave) return
        if (deckId <= 0) {
            _uiState.update {
                it.copy(
                    phase = GeneratePhase.Error,
                    errorMessage = "Deck ID tidak valid. Kembali ke layar sebelumnya.",
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(phase = GeneratePhase.Saving) }
            try {
                // Filter drafts yang valid (front & back non-blank).
                // User mungkin lupa fill setelah edit — silently skip.
                val validDrafts = current.drafts.filter {
                    it.front.isNotBlank() && it.back.isNotBlank()
                }

                if (validDrafts.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            phase = GeneratePhase.Preview,
                            errorMessage = "Tidak ada kartu valid. Pastikan front & back terisi.",
                        )
                    }
                    return@launch
                }

                // Bulk insert via existing createCards() yang sudah pakai transaction.
                val pairs = validDrafts.map { it.front.trim() to it.back.trim() }
                cardRepository.createCards(deckId = deckId, cards = pairs)

                _uiState.update {
                    it.copy(
                        phase = GeneratePhase.Done,
                        savedCardsCount = validDrafts.size,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        phase = GeneratePhase.Preview,
                        errorMessage = e.message ?: "Gagal menyimpan kartu. Coba lagi.",
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // PHASE: Error → Retry
    // ════════════════════════════════════════════════════════════════════════

    /** Kembali ke Input phase untuk re-edit material & retry generate. */
    fun retryFromInput() {
        _uiState.update {
            it.copy(phase = GeneratePhase.Input, errorMessage = null)
        }
    }
}