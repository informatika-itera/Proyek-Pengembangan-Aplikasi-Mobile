package com.example.tabungin.presentation.screens.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.usecase.GetAllSetoranUseCase
import kotlinx.coroutines.flow.*

private fun formatRupiah(amount: Double): String {
    val formatted = amount.toLong().toString()
        .reversed().chunked(3).joinToString(".").reversed()
    return "Rp $formatted"
}

data class RiwayatUiState(
    val setoranList: List<Setoran> = emptyList(),
    val isLoading: Boolean         = true,
    val totalSetoran: Double       = 0.0
)

class RiwayatViewModel(
    private val getAllSetoranUseCase: GetAllSetoranUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RiwayatUiState())
    val uiState: StateFlow<RiwayatUiState> = _uiState.asStateFlow()

    init {
        getAllSetoranUseCase()
            .onEach { list ->
                _uiState.update {
                    it.copy(
                        setoranList  = list,
                        isLoading    = false,
                        totalSetoran = list.sumOf { s -> s.amount }
                    )
                }
            }
            .catch { _uiState.update { it.copy(isLoading = false) } }
            .launchIn(viewModelScope)
    }

    /**
     * Export setoran history to CSV format string
     */
    fun exportToCsv(): String {
        val setoranList = _uiState.value.setoranList
        if (setoranList.isEmpty()) return ""

        val sb = StringBuilder()
        // CSV Header
        sb.appendLine("ID,Tanggal,Jumlah,Catatan,Target ID,Tanggal Dibuat")

        // CSV Data rows
        setoranList.forEach { setoran ->
            val catatan = setoran.catatan.replace(",", ";").replace("\n", " ")
            sb.appendLine("${setoran.id},${setoran.tanggal},${setoran.amount},$catatan,${setoran.targetId},${setoran.createdAt}")
        }

        return sb.toString()
    }

    /**
     * Export setoran history to formatted text for sharing
     */
    fun exportToText(): String {
        val setoranList = _uiState.value.setoranList
        val total = _uiState.value.totalSetoran

        if (setoranList.isEmpty()) return "Belum ada riwayat setoran"

        val sb = StringBuilder()
        sb.appendLine("📊 RIWAYAT TABUNGAN")
        sb.appendLine("========================")
        sb.appendLine("Total: ${formatRupiah(total)}")
        sb.appendLine("Jumlah Transaksi: ${setoranList.size}")
        sb.appendLine("")
        sb.appendLine("Daftar Transaksi:")
        sb.appendLine("------------------------")

        setoranList.sortedByDescending { it.tanggal }.forEachIndexed { index, setoran ->
            sb.appendLine("${index + 1}. ${setoran.tanggal} - ${formatRupiah(setoran.amount)}")
            if (setoran.catatan.isNotBlank()) {
                sb.appendLine("   Catatan: ${setoran.catatan}")
            }
        }

        sb.appendLine("------------------------")
        sb.appendLine("Export dari TabungIn App")

        return sb.toString()
    }

    /**
     * Generate summary text for sharing
     */
    fun generateSummary(): String {
        val setoranList = _uiState.value.setoranList
        val total = _uiState.value.totalSetoran

        if (setoranList.isEmpty()) {
            return "Saya belum punya riwayat tabungan di TabungIn. Yuk mulai menabung!"
        }

        val avgPerTransaction = if (setoranList.isNotEmpty()) total / setoranList.size else 0.0

        return buildString {
            appendLine("💰 Summary Tabungan Saya")
            appendLine("")
            appendLine("📈 Total Tabungan: ${formatRupiah(total)}")
            appendLine("📋 Total Transaksi: ${setoranList.size}")
            appendLine("📊 Rata-rata per Transaksi: ${formatRupiah(avgPerTransaction)}")
            appendLine("")
            appendLine("💡 ${if (setoranList.size >= 10) "Hebat! Kamu sudah konsisten menabung." else "Terus semangat menabung untuk mencapai impianmu!"}")
            appendLine("")
            appendLine("TabungIn - Wujudkan Impianmu!")
        }
    }
}