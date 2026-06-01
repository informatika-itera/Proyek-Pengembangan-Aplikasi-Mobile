package com.example.tabungin.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.repository.AIRepository
import com.example.tabungin.domain.repository.TargetRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.random.Random

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val targetRepository: TargetRepository?
) : ViewModel() {

    private var messageIdCounter = 0L
    private fun nextId() = ++messageIdCounter

    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AIAssistantEvent>()
    val events: SharedFlow<AIAssistantEvent> = _events.asSharedFlow()

    init {
        loadTargets()
    }

    private fun loadTargets() {
        viewModelScope.launch {
            try {
                val targets = targetRepository?.getAllTargets()?.first() ?: emptyList()
                val setoran = targetRepository?.getAllSetoran()?.first() ?: emptyList()
                _uiState.update { it.copy(targets = targets, setoran = setoran) }
            } catch (e: Exception) {
                // Handle silently
            }
        }
    }

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onSuggestionClick(suggestion: String) {
        // Add user message
        _uiState.update { state ->
            state.copy(
                messages = state.messages + ChatMessage(
                    content = suggestion,
                    isUser = true,
                    timestamp = nextId()
                )
            )
        }
        sendToAI(suggestion)
    }

    fun sendMessage() {
        val input = _uiState.value.inputText
        if (input.isBlank()) return

        // Add user message
        _uiState.update { state ->
            state.copy(
                messages = state.messages + ChatMessage(
                    content = input,
                    isUser = true,
                    timestamp = nextId()
                ),
                inputText = ""
            )
        }
        sendToAI(input)
    }

    private fun sendToAI(message: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val context = buildContext()
                val prompt = if (context.isNotEmpty()) {
                    "$context\n\nUser: $message"
                } else {
                    message
                }

                val result = aiRepository.chat(prompt, isTabunganContext = true)
                result.onSuccess { response ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            messages = state.messages + ChatMessage(
                                content = response,
                                isUser = false,
                                timestamp = nextId()
                            )
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            messages = state.messages + ChatMessage(
                                content = "Maaf, terjadi kesalahan: ${error.message}",
                                isUser = false,
                                timestamp = nextId()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        messages = state.messages + ChatMessage(
                            content = "Maaf, terjadi kesalahan: ${e.message}",
                            isUser = false,
                            timestamp = nextId()
                        )
                    )
                }
            }
        }
    }

    private fun buildContext(): String {
        val state = _uiState.value
        if (state.targets.isEmpty()) return ""

        val totalTabungan = state.targets.sumOf { it.terkumpul }
        val totalTarget = state.targets.sumOf { it.targetAmount }
        val targetsTercapai = state.targets.count { it.terkumpul >= it.targetAmount }

        // Parse deadline dates and calculate days remaining
        val today = java.time.LocalDate.now()
        val targetsWithDeadlineInfo = state.targets.map { target ->
            val deadlineInfo = try {
                val parts = target.deadline.split("-")
                val deadlineDate = java.time.LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                val daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, deadlineDate).toInt()
                val formattedDate = formatDeadlineIndonesia(deadlineDate)
                Triple(formattedDate, daysRemaining, deadlineDate)
            } catch (e: Exception) {
                Triple(target.deadline, -1, null)
            }
            target to deadlineInfo
        }

        // Sort by deadline date (nearest first)
        val sortedTargets = targetsWithDeadlineInfo
            .filter { it.second.third != null }
            .sortedBy { it.second.third }

        val targetsInfo = sortedTargets.joinToString("\n") { (target, info) ->
            val (formattedDate, daysRemaining, _) = info
            val progress = if (target.targetAmount > 0) ((target.terkumpul / target.targetAmount) * 100).toInt() else 0
            val status = if (target.terkumpul >= target.targetAmount) "[TERCAPAI]" else "[BELUM]"
            val daysText = when {
                daysRemaining < 0 -> "(LEWAT)"
                daysRemaining == 0 -> "(HARI INI)"
                daysRemaining == 1 -> "(1 hari lagi)"
                else -> "($daysRemaining hari lagi)"
            }
            "- $status ${target.icon} ${target.nama}: Rp ${formatNumber(target.terkumpul.toLong())} / Rp ${formatNumber(target.targetAmount.toLong())} ($progress%) | Deadline: $formattedDate $daysText"
        }

        // Find the nearest deadline
        val nearestTarget = sortedTargets.firstOrNull()
        val nearestInfo = nearestTarget?.second ?: null

        return """
Sumber data tabungan:
- Total Tabungan: Rp ${formatNumber(totalTabungan.toLong())}
- Total Target: Rp ${formatNumber(totalTarget.toLong())}
- Target Tercapai: $targetsTercapai dari ${state.targets.size}
- Total Target Aktif: ${state.targets.size}

Daftar Target (diurutkan berdasarkan deadline terdekat):
$targetsInfo

${if (nearestTarget != null) """
TARGET DENGAN DEADLINE TERDEKAT:
- ${nearestTarget.first.icon} ${nearestTarget.first.nama}: ${nearestInfo?.first ?: ""} (${getDaysText(nearestInfo?.second ?: 0)})
  Progress: Rp ${formatNumber(nearestTarget.first.terkumpul.toLong())} / Rp ${formatNumber(nearestTarget.first.targetAmount.toLong())}
  Sisa: Rp ${formatNumber(nearestTarget.first.sisaTabungan.toLong())}
""" else ""}

PENTING: Gunakan data di atas untuk menjawab pertanyaan tentang target dengan deadline terdekat.
Format tanggal deadline adalah: tanggal bulan tahun (contoh: 31 Desember 2025).
Jumlah hari tersisa dihitung dari hari ini ($today).
        """.trimIndent()
    }

    private fun formatDeadlineIndonesia(date: java.time.LocalDate): String {
        val bulanIndonesia = mapOf(
            1 to "Januari", 2 to "Februari", 3 to "Maret", 4 to "April",
            5 to "Mei", 6 to "Juni", 7 to "Juli", 8 to "Agustus",
            9 to "September", 10 to "Oktober", 11 to "November", 12 to "Desember"
        )
        return "${date.dayOfMonth} ${bulanIndonesia[date.monthValue]} ${date.year}"
    }

    private fun getDaysText(days: Int): String {
        return when {
            days < 0 -> "sudah lewat ${-days} hari"
            days == 0 -> "hari ini"
            days == 1 -> "1 hari lagi"
            else -> "$days hari lagi"
        }
    }

    private fun formatNumber(number: Long): String {
        return number.toString().reversed().chunked(3).joinToString(".").reversed()
    }

    fun copyMessage(message: ChatMessage) {
        viewModelScope.launch {
            _events.emit(AIAssistantEvent.CopyToClipboard(message.content))
        }
    }

    fun clearChat() {
        _uiState.update { it.copy(messages = emptyList()) }
    }
}

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long
)

data class AIAssistantUiState(
    val inputText: String = "",
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val targets: List<Target> = emptyList(),
    val setoran: List<Setoran> = emptyList()
)

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyToNote(val text: String) : AIAssistantEvent
}