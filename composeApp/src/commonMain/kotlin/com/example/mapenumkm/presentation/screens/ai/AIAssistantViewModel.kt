package com.example.mapenumkm.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.repository.AIRepository
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.repository.TransactionRepository
import com.example.mapenumkm.domain.usecase.GenerateIdeasUseCase
import com.example.mapenumkm.domain.usecase.ImproveWritingUseCase
import com.example.mapenumkm.domain.usecase.SummarizeNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isSummary: Boolean = false,
    val summaryData: BusinessSummary? = null
)

data class BusinessSummary(
    val totalSales: Double,
    val transactionCount: Int,
    val topProduct: String,
    val topProductSales: Int,
    val lowStockProducts: List<Note>,
    val peakHours: String
)

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val summarizeUseCase: SummarizeNoteUseCase,
    private val improveWritingUseCase: ImproveWritingUseCase,
    private val generateIdeasUseCase: GenerateIdeasUseCase,
    private val transactionRepository: TransactionRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AIAssistantEvent>()
    val events: SharedFlow<AIAssistantEvent> = _events.asSharedFlow()
    
    init {
        generateInitialSummary()
    }

    private fun generateInitialSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val transactions = transactionRepository.getAllTransactions().first()
            val notes = noteRepository.getAllNotes().first()
            
            val systemTZ = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(systemTZ).date
            
            val todayTransactions = transactions.filter {
                it.createdAt.toLocalDateTime(systemTZ).date == today
            }
            
            val totalSales = todayTransactions.sumOf { it.total }
            val transactionCount = todayTransactions.size
            
            // Calculate top product
            val productSales = mutableMapOf<String, Int>()
            todayTransactions.forEach { t ->
                t.items.forEach { item ->
                    productSales[item.productName] = (productSales[item.productName] ?: 0) + item.quantity
                }
            }
            val topProductEntry = productSales.maxByOrNull { it.value }
            
            // Low stock
            val lowStock = notes.filter { it.stock <= 5 }
            
            // Peak hours
            val hourlySales = IntArray(24)
            todayTransactions.forEach {
                hourlySales[it.createdAt.toLocalDateTime(systemTZ).hour]++
            }
            val peakHourStart = hourlySales.indices.maxByOrNull { hourlySales[it] } ?: 0
            val peakHoursStr = "${peakHourStart.toString().padStart(2, '0')}:00 - ${(peakHourStart + 2).toString().padStart(2, '0')}:00"

            val summary = BusinessSummary(
                totalSales = totalSales,
                transactionCount = transactionCount,
                topProduct = topProductEntry?.key ?: "-",
                topProductSales = topProductEntry?.value ?: 0,
                lowStockProducts = lowStock,
                peakHours = peakHoursStr
            )

            val welcomeMsg = ChatMessage(
                text = "Halo Owner! 👋\nBerikut ringkasan bisnis kamu hari ini.",
                isUser = false
            )
            
            val summaryMsg = ChatMessage(
                text = "",
                isUser = false,
                isSummary = true,
                summaryData = summary
            )

            _uiState.update { 
                it.copy(
                    messages = listOf(welcomeMsg, summaryMsg),
                    isLoading = false
                )
            }
        }
    }
    
    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }
    
    fun sendMessage() {
        val text = _uiState.value.inputText
        if (text.isBlank()) return
        
        val userMsg = ChatMessage(text, true)
        _uiState.update { 
            it.copy(
                messages = it.messages + userMsg,
                inputText = "",
                isLoading = true
            )
        }
        
        viewModelScope.launch {
            val transactions = transactionRepository.getAllTransactions().first()
            val notes = noteRepository.getAllNotes().first()
            
            val systemTZ = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(systemTZ).date
            
            val todayTransactions = transactions.filter {
                it.createdAt.toLocalDateTime(systemTZ).date == today
            }
            
            val totalSales = todayTransactions.sumOf { it.total }
            val lowStock = notes.filter { it.stock <= 5 }
            
            // Calculate top product
            val productSales = mutableMapOf<String, Int>()
            todayTransactions.forEach { t ->
                t.items.forEach { item ->
                    productSales[item.productName] = (productSales[item.productName] ?: 0) + item.quantity
                }
            }
            val topProductEntry = productSales.maxByOrNull { it.value }
            
            val businessContext = """
                Ringkasan Bisnis Hari Ini (${today}):
                - Total Pendapatan: Rp${totalSales}
                - Jumlah Transaksi: ${todayTransactions.size}
                - Produk Terlaris: ${topProductEntry?.key ?: "Belum ada"} (${topProductEntry?.value ?: 0} terjual)
                - Produk Stok Menipis: ${lowStock.joinToString { "${it.title} (Sisa ${it.stock})" }.ifEmpty { "Semua stok aman" }}
                
                Fitur Aplikasi MaPen UMKM:
                1. Dashboard: Ringkasan performa bisnis.
                2. Manajemen Produk: Tambah/Edit/Hapus produk dan stok.
                3. Transaksi: Pencatatan penjualan dan hitung kembalian.
                4. Riwayat: Daftar transaksi terdahulu.
                5. Laporan: Statistik harian, mingguan, bulanan.
            """.trimIndent()

            val result = aiRepository.businessChat(text, businessContext)
            result.onSuccess { response ->
                val aiMsg = ChatMessage(response, false)
                _uiState.update { it.copy(messages = it.messages + aiMsg, isLoading = false) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, error = err.message) }
            }
        }
    }

    fun onQuickAction(action: String) {
        _uiState.update { it.copy(inputText = action) }
        sendMessage()
    }
}

data class AIAssistantUiState(
    val inputText: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyToNote(val text: String) : AIAssistantEvent
}
