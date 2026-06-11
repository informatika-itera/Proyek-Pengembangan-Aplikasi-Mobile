package com.example.inventra.presentation.screens.ai

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.core.localization.Strings
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.repository.AIRepository
import com.example.inventra.domain.repository.ItemRepository
import com.example.inventra.domain.repository.BorrowRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ==================== UI STATE ====================

data class AIInventoryUiState(
    val inputText: TextFieldValue = TextFieldValue(""),
    val selectedAction: InventoryAIAction = InventoryAIAction.ANALYZE_STOCK,
    val isLoading: Boolean = false,
    val result: String? = null,
    val error: String? = null,
    val isLoadingItems: Boolean = false
) {
    val canExecute: Boolean
        get() = !isLoading && !isLoadingItems
}

sealed interface AIInventoryEvent {
    data class ShowSnackbar(val message: String) : AIInventoryEvent
}

// ==================== AI ACTIONS ====================

enum class InventoryAIAction(
    val needsInput: Boolean = false
) {
    ANALYZE_STOCK,
    SUGGEST_PROCUREMENT,
    BORROWING_REPORT,
    OVERDUE_ACTION,
    CUSTOM_QUERY(needsInput = true);
    
    fun getDisplayName(strings: Strings): String = when(this) {
        ANALYZE_STOCK -> strings.aiAnalyzeStock
        SUGGEST_PROCUREMENT -> strings.aiSuggestProcurement
        BORROWING_REPORT -> strings.aiBorrowingReport
        OVERDUE_ACTION -> strings.aiOverdueAction
        CUSTOM_QUERY -> strings.aiCustomQuery
    }
    
    fun getDescription(strings: Strings): String = when(this) {
        ANALYZE_STOCK -> strings.aiAnalyzeStockDesc
        SUGGEST_PROCUREMENT -> strings.aiSuggestProcurementDesc
        BORROWING_REPORT -> strings.aiBorrowingReportDesc
        OVERDUE_ACTION -> strings.aiOverdueActionDesc
        CUSTOM_QUERY -> strings.aiCustomQueryDesc
    }
}

// ==================== VIEWMODEL ====================

class AIInventoryViewModel(
    private val aiRepository: AIRepository,
    private val itemRepository: ItemRepository,
    private val borrowRepository: BorrowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIInventoryUiState())
    val uiState: StateFlow<AIInventoryUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AIInventoryEvent>()
    val events: SharedFlow<AIInventoryEvent> = _events.asSharedFlow()

    // Cache data inventaris
    private var cachedItems: List<Item> = emptyList()
    private var cachedBorrowRecords: List<BorrowRecord> = emptyList()

    init {
        loadInventoryData()
    }

    private fun loadInventoryData() {
        _uiState.update { it.copy(isLoadingItems = true) }
        viewModelScope.launch {
            try {
                cachedItems = itemRepository.getAllItems().first()
                cachedBorrowRecords = borrowRepository.getAllRecords().first()
            } catch (e: Exception) {
                // Data mungkin kosong, tidak masalah
            } finally {
                _uiState.update { it.copy(isLoadingItems = false) }
            }
        }
    }

    fun onInputTextChange(text: TextFieldValue) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }

    fun onActionSelected(action: InventoryAIAction) {
        _uiState.update { it.copy(selectedAction = action, result = null, error = null) }
    }

    fun executeAction(strings: Strings) {
        val state = _uiState.value

        if (state.selectedAction.needsInput && state.inputText.text.isBlank()) {
            _uiState.update { it.copy(error = strings.aiCustomQueryHint) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, result = null) }

        viewModelScope.launch {
            val prompt = buildPrompt(state.selectedAction, state.inputText.text, strings)

            aiRepository.chat(prompt)
                .onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, result = result) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error"
                        )
                    }
                }
        }
    }

    private fun buildPrompt(action: InventoryAIAction, userInput: String, strings: Strings): String {
        val inventarySummary = buildInventarySummary()

        val systemContext = """
            Kamu adalah asisten manajemen inventaris untuk Kabinet Nexara HMIF ITERA 2026.
            Tugasmu adalah membantu pengurus dalam mengelola aset organisasi dengan memberikan informasi yang singkat, padat, dan jelas.
            
            ATURAN PENTING:
            1. JANGAN gunakan format Markdown (seperti #, ##, ***, atau tabel).
            2. Gunakan gaya bahasa chat biasa yang santai namun tetap sopan.
            3. Jawablah langsung ke intinya, hindari basa-basi yang panjang.
            4. Gunakan list sederhana dengan simbol peluru (•) atau penomoran biasa jika diperlukan.
            5. Pastikan jawaban lengkap dan tidak terpotong, meskipun singkat.
            6. ${strings.aiSystemContext}

            DATA INVENTARIS SAAT INI:
            $inventarySummary
        """.trimIndent()

        val taskPrompt = when (action) {
            InventoryAIAction.ANALYZE_STOCK -> """
                Analisis stok inventaris saat ini secara singkat.
                Berikan ringkasan total barang, item yang stoknya kritis (sedikit/habis), dan rekomendasi singkat untuk pengelola.
            """.trimIndent()

            InventoryAIAction.SUGGEST_PROCUREMENT -> """
                Berikan saran pengadaan barang yang mendesak berdasarkan data stok.
                Sebutkan nama barang dan jumlah yang disarankan untuk ditambah.
            """.trimIndent()

            InventoryAIAction.BORROWING_REPORT -> """
                Berikan ringkasan laporan peminjaman. 
                Sebutkan berapa yang aktif, selesai, dan overdue secara singkat.
            """.trimIndent()

            InventoryAIAction.OVERDUE_ACTION -> """
                Sebutkan daftar peminjaman yang overdue dan berikan satu saran tindakan cepat untuk menanganinya.
            """.trimIndent()

            InventoryAIAction.CUSTOM_QUERY -> """
                Pertanyaan: $userInput
                Jawablah secara singkat dan akurat berdasarkan data yang ada.
            """.trimIndent()
        }

        return "$systemContext\n\n$taskPrompt"
    }

    private fun buildInventarySummary(): String {
        if (cachedItems.isEmpty() && cachedBorrowRecords.isEmpty()) {
            return "Belum ada data inventaris yang tersedia."
        }

        val sb = StringBuilder()

        // Item summary
        sb.appendLine("=== DATA BARANG (${cachedItems.size} item) ===")
        if (cachedItems.isNotEmpty()) {
            val totalStock = cachedItems.sumOf { it.totalStock }
            val availableStock = cachedItems.sumOf { it.availableStock }
            val borrowedStock = totalStock - availableStock

            sb.appendLine("Total stok keseluruhan: $totalStock unit")
            sb.appendLine("Stok tersedia: $availableStock unit")
            sb.appendLine("Sedang dipinjam: $borrowedStock unit")
            sb.appendLine()

            // Per kategori
            sb.appendLine("Per kategori:")
            cachedItems.groupBy { it.category }.forEach { (cat, items) ->
                sb.appendLine("  - ${cat.displayName}: ${items.size} jenis barang")
            }
            sb.appendLine()

            // Detail per item
            sb.appendLine("Detail barang:")
            cachedItems.forEach { item ->
                sb.appendLine(
                    "  • ${item.name} | Kategori: ${item.category.displayName} | " +
                    "Stok: ${item.availableStock}/${item.totalStock} | " +
                    "Kondisi: ${item.condition.displayName} | " +
                    "Lokasi: ${item.location} | " +
                    "PIC: ${item.picName.ifBlank { "N/A" }}"
                )
            }
        } else {
            sb.appendLine("Belum ada data barang.")
        }

        // Borrow records summary
        sb.appendLine()
        sb.appendLine("=== DATA PEMINJAMAN (${cachedBorrowRecords.size} record) ===")
        if (cachedBorrowRecords.isNotEmpty()) {
            val activeCount = cachedBorrowRecords.count { it.status.name == "ACTIVE" }
            val returnedCount = cachedBorrowRecords.count { it.status.name == "RETURNED" }
            val overdueCount = cachedBorrowRecords.count { it.status.name == "OVERDUE" }

            sb.appendLine("Aktif: $activeCount | Selesai: $returnedCount | Overdue: $overdueCount")
            sb.appendLine()

            if (cachedBorrowRecords.isNotEmpty()) {
                sb.appendLine("Detail peminjaman:")
                cachedBorrowRecords.take(20).forEach { record -> // Batasi agar prompt tidak terlalu panjang
                    sb.appendLine(
                        "  • ${record.itemName} | Peminjam: ${record.borrowerName} | " +
                        "Status: ${record.status.name} | " +
                        "Denda: Rp ${record.fineAmount}"
                    )
                }
                if (cachedBorrowRecords.size > 20) {
                    sb.appendLine("  ... dan ${cachedBorrowRecords.size - 20} record lainnya")
                }
            }
        } else {
            sb.appendLine("Belum ada data peminjaman.")
        }

        return sb.toString()
    }
}
