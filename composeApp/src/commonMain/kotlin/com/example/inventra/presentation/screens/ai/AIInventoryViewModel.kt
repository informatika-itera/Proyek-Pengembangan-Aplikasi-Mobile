package com.example.inventra.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val inputText: String = "",
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
    val displayName: String,
    val description: String,
    val needsInput: Boolean = false,
    val inputHint: String = ""
) {
    ANALYZE_STOCK(
        displayName = "Analisis Stok",
        description = "Analisis kondisi stok inventaris saat ini dan berikan rekomendasi"
    ),
    SUGGEST_PROCUREMENT(
        displayName = "Saran Pengadaan",
        description = "Dapatkan saran barang apa yang perlu diadakan berdasarkan data"
    ),
    BORROWING_REPORT(
        displayName = "Laporan Peminjaman",
        description = "Ringkasan dan analisis pola peminjaman barang"
    ),
    OVERDUE_ACTION(
        displayName = "Tindak Overdue",
        description = "Saran tindakan untuk barang yang terlambat dikembalikan"
    ),
    CUSTOM_QUERY(
        displayName = "Tanya Bebas",
        description = "Tanyakan apapun tentang inventaris Anda",
        needsInput = true,
        inputHint = "Contoh: Barang apa yang paling sering dipinjam? Apa saran untuk meningkatkan pengelolaan?"
    )
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

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }

    fun onActionSelected(action: InventoryAIAction) {
        _uiState.update { it.copy(selectedAction = action, result = null, error = null) }
    }

    fun executeAction() {
        val state = _uiState.value

        if (state.selectedAction.needsInput && state.inputText.isBlank()) {
            _uiState.update { it.copy(error = "Masukkan pertanyaan terlebih dahulu") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, result = null) }

        viewModelScope.launch {
            val prompt = buildPrompt(state.selectedAction, state.inputText)

            aiRepository.chat(prompt)
                .onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, result = result) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Terjadi kesalahan saat menghubungi AI"
                        )
                    }
                }
        }
    }

    private fun buildPrompt(action: InventoryAIAction, userInput: String): String {
        val inventarySummary = buildInventarySummary()

        val systemContext = """
            Kamu adalah asisten manajemen inventaris untuk HMIF ITERA (Himpunan Mahasiswa Informatika Institut Teknologi Sumatera).
            Kamu membantu pengelolaan aset dan barang-barang organisasi.
            Selalu gunakan Bahasa Indonesia yang jelas dan profesional.
            Berikan jawaban yang terstruktur, praktis, dan actionable.
            
            DATA INVENTARIS SAAT INI:
            $inventarySummary
        """.trimIndent()

        val taskPrompt = when (action) {
            InventoryAIAction.ANALYZE_STOCK -> """
                Lakukan analisis mendalam terhadap kondisi stok inventaris di atas.
                
                Sertakan dalam analisis:
                1. **Ringkasan Kondisi** — overview total item, stok tersedia vs dipinjam
                2. **Item Kritis** — barang dengan stok rendah atau kondisi buruk
                3. **Distribusi Kategori** — apakah ada kategori yang kurang terwakili?
                4. **Kesehatan Inventaris** — penilaian keseluruhan (Baik/Perlu Perhatian/Kritis)
                5. **Rekomendasi Prioritas** — 3 tindakan utama yang perlu dilakukan
                
                Format jawaban dengan header yang jelas dan poin-poin terstruktur.
            """.trimIndent()

            InventoryAIAction.SUGGEST_PROCUREMENT -> """
                Berdasarkan data inventaris di atas, berikan saran pengadaan barang.
                
                Analisis dan rekomendasikan:
                1. **Barang yang Habis/Kritis** — item yang stoknya menipis dan perlu segera diadakan
                2. **Barang Baru yang Disarankan** — berdasarkan kategori yang ada, apa yang mungkin dibutuhkan HMIF?
                3. **Prioritas Pengadaan** — urutkan dari paling mendesak
                4. **Estimasi Kebutuhan** — berapa unit yang disarankan untuk diadakan
                5. **Tips Pengelolaan** — saran untuk menjaga ketersediaan stok
                
                Pertimbangkan konteks sebagai organisasi mahasiswa yang memiliki keterbatasan anggaran.
            """.trimIndent()

            InventoryAIAction.BORROWING_REPORT -> """
                Buat laporan dan analisis peminjaman berdasarkan data di atas.
                
                Laporan harus mencakup:
                1. **Statistik Peminjaman** — total aktif, selesai, overdue
                2. **Pola Peminjaman** — barang apa yang paling sering dipinjam?
                3. **Status Overdue** — daftar dan kondisi peminjaman yang terlambat
                4. **Analisis Peminjam** — siapa yang paling sering meminjam?
                5. **Rekomendasi Kebijakan** — saran untuk meningkatkan disiplin pengembalian
                6. **Ringkasan Denda** — total potensi denda dari overdue
                
                Sampaikan dengan format yang mudah dipresentasikan ke rapat organisasi.
            """.trimIndent()

            InventoryAIAction.OVERDUE_ACTION -> """
                Berikan panduan tindakan untuk menangani peminjaman overdue berdasarkan data di atas.
                
                Sertakan:
                1. **Daftar Overdue** — semua peminjaman yang melewati batas waktu
                2. **Prioritas Penagihan** — urutkan berdasarkan lama keterlambatan
                3. **Template Pesan** — contoh pesan notifikasi yang sopan tapi tegas
                4. **Prosedur Penanganan** — langkah-langkah yang disarankan
                5. **Perhitungan Denda** — estimasi denda berdasarkan aturan (Rp 10.000/hari)
                6. **Pencegahan ke Depan** — saran agar overdue berkurang di masa mendatang
                
                Tone: profesional dan tegas namun tetap collegial sesama mahasiswa.
            """.trimIndent()

            InventoryAIAction.CUSTOM_QUERY -> """
                Pertanyaan dari pengelola inventaris: $userInput
                
                Jawab pertanyaan ini berdasarkan data inventaris yang tersedia.
                Berikan jawaban yang spesifik, akurat berdasarkan data, dan actionable.
                Jika pertanyaan tidak bisa dijawab dengan data yang ada, jelaskan apa data tambahan yang dibutuhkan.
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
