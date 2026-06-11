package com.example.hujjah.presentation.screens.lens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hujjah.domain.model.islamic.ChatMessage
import com.example.hujjah.domain.model.islamic.IslamicReference
import com.example.hujjah.domain.model.islamic.Sender
import com.example.hujjah.domain.model.islamic.SourceType
import com.example.hujjah.domain.repository.AIRepository
import com.example.hujjah.domain.repository.hujjah.BookmarkRepository
import com.example.hujjah.domain.repository.hujjah.HujjahRepository
import com.example.hujjah.core.network.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class HujjahLensUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOffline: Boolean = false
)

@Serializable
private data class GeminiIslamicResponse(
    val counselorResponse: String,
    val solutions: List<String> = emptyList(),
    val references: List<GeminiReferenceItem> = emptyList()
)

@Serializable
private data class GeminiReferenceItem(
    val sourceType: String, // QURAN or HADITH
    val title: String,
    val sourceName: String,
    val surahNumber: Int? = null,
    val arabicText: String,
    val translation: String,
    val explanation: String
)

class HujjahLensViewModel(
    private val hujjahRepository: HujjahRepository,
    private val aiRepository: AIRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(HujjahLensUiState())
    val uiState: StateFlow<HujjahLensUiState> = _uiState.asStateFlow()

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    init {
        loadChatHistory()
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _uiState.value = _uiState.value.copy(
                    isOffline = !online
                )
            }
        }
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            hujjahRepository.getChatHistory().collect { history ->
                if (history.isEmpty()) {
                    val welcomeMsg = ChatMessage(
                        id = "welcome",
                        sender = Sender.AI,
                        text = "Assalamualaikum, saya adalah Hujjah Lens. Ceritakan apa yang sedang mengganjal di hatimu saat ini, atau tanyakan apa saja seputar pandangan Islam. Saya akan mendengarkan dan mencarikan dalil yang tepat untukmu.",
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                    hujjahRepository.saveChatMessage(welcomeMsg)
                } else {
                    _uiState.value = _uiState.value.copy(
                        messages = history
                    )
                }
            }
        }
    }

    fun onInputTextChanged(value: String) {
        _uiState.value = _uiState.value.copy(inputText = value)
    }

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id = "msg-${Clock.System.now().toEpochMilliseconds()}",
            sender = Sender.USER,
            text = text,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )

        if (_uiState.value.isOffline) {
            val offlineErrorMsg = ChatMessage(
                id = "msg-${Clock.System.now().toEpochMilliseconds()}-err",
                sender = Sender.AI,
                text = "Koneksi internet terputus atau tidak stabil. Silakan periksa koneksi Anda dan coba lagi.",
                timestamp = Clock.System.now().toEpochMilliseconds() + 50
            )
            viewModelScope.launch {
                hujjahRepository.saveChatMessage(userMessage)
                hujjahRepository.saveChatMessage(offlineErrorMsg)
            }
            return
        }

        _uiState.value = _uiState.value.copy(
            inputText = "",
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            hujjahRepository.saveChatMessage(userMessage)

            val systemPrompt = """
                Kamu adalah Hujjah Lens, asisten spiritual Islam yang cerdas, hangat, dan bersahabat.
                Jawablah seperti AI pada umumnya: natural, mengalir, dan tidak kaku (jangan menggunakan format kaku atau template).
                
                ATURAN UTAMA:
                1. Jika pengguna hanya menyapa (Halo, Hai, dll), balaslah dengan ramah tanpa perlu mengaitkan dengan dalil.
                2. JIKA pengguna curhat masalah pribadi atau meminta nasihat agama, DAN kamu merasa ada dalil (Al-Qur'an/Hadis) yang relevan, TULISKAN dalil tersebut beserta terjemahannya SECARA LANGSUNG menyatu di dalam paragraf jawabanmu.
                3. JIKA TIDAK ADA dalil yang relevan, atau pengguna hanya sekadar bertanya hal umum, cukup berikan jawaban atau nasihat yang bijak tanpa memaksakan mengeluarkan dalil. Sesuaikan saja berdasarkan kebutuhan percakapan.
                4. PENTING UNTUK NAVIGASI APLIKASI: JIKA kamu merekomendasikan membaca suatu surah/ayat Al-Qur'an, tambahkan KODE RAHASIA ini di paling akhir jawabanmu (di baris baru):
                [NAVIGASI: QS. NamaSurah: Ayat (surahNumber: X, verseNumber: Y)]
                Contoh: [NAVIGASI: QS. Al-Baqarah: 153 (surahNumber: 2, verseNumber: 153)]
                
                Pastikan X adalah nomor surah (1-114) dan Y adalah nomor ayat. Jika hanya menyarankan surah utuh tanpa ayat spesifik, tidak perlu mencantumkan verseNumber, contoh: [NAVIGASI: QS. Al-Baqarah (surahNumber: 2)]. Kode ini tidak akan terlihat oleh pengguna, tapi akan diubah menjadi tombol navigasi.
            """.trimIndent()

            val historyText = _uiState.value.messages
                .filter { it.id != "welcome" && it.id != userMessage.id }
                .takeLast(6)
                .joinToString("\n") { msg ->
                    val roleName = if (msg.sender == Sender.USER) "User" else "Hujjah Lens"
                    "$roleName: ${msg.text}"
                }

            val promptInput = if (historyText.isBlank()) {
                "User: \"$text\""
            } else {
                "Riwayat Obrolan:\n$historyText\n\nUser: \"$text\""
            }

            val result = aiRepository.chat(
                message = promptInput,
                systemPrompt = systemPrompt
            )

            result.fold(
                onSuccess = { responseText ->
                    if (responseText.isBlank()) {
                        val errorMessage = ChatMessage(
                            id = "msg-${Clock.System.now().toEpochMilliseconds()}-err",
                            sender = Sender.AI,
                            text = "Maaf, balasan dari server kosong. Silakan coba lagi.",
                            timestamp = Clock.System.now().toEpochMilliseconds()
                        )
                        viewModelScope.launch { hujjahRepository.saveChatMessage(errorMessage) }
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        return@fold
                    }

                    val lines = responseText.lines()
                    val messageTextLines = mutableListOf<String>()
                    val parsedReferences = mutableListOf<IslamicReference>()

                    for (line in lines) {
                        val trimmedLine = line.trim()
                        if (trimmedLine.startsWith("[NAVIGASI:", ignoreCase = true)) {
                            // Extract surahNumber and verseNumber using regex
                            val surahNumberRegex = Regex("surahNumber:\\s*(\\d+)")
                            val verseNumberRegex = Regex("verseNumber:\\s*(\\d+)")
                            
                            val surahNumber = surahNumberRegex.find(trimmedLine)?.groupValues?.get(1)?.toIntOrNull()
                            val verseNumber = verseNumberRegex.find(trimmedLine)?.groupValues?.get(1)?.toIntOrNull()
                            
                            // Extract sourceName (e.g. "QS. Al-Baqarah: 153")
                            val rawContent = trimmedLine.substringAfter("[NAVIGASI:").substringBefore("(").trim()
                            val sourceName = rawContent.replace("]", "").trim()
                            
                            parsedReferences.add(
                                IslamicReference(
                                    id = "nav-${Clock.System.now().toEpochMilliseconds()}-${sourceName.hashCode()}",
                                    sourceType = SourceType.QURAN,
                                    title = "Buka $sourceName",
                                    sourceName = sourceName,
                                    arabicText = "", // We no longer display this in the card
                                    translation = "", // We no longer display this in the card
                                    explanation = "", // We no longer display this in the card
                                    topicId = "dynamic",
                                    topicTitle = "Rekomendasi AI",
                                    surahNumber = surahNumber,
                                    verseNumber = verseNumber
                                )
                            )
                        } else {
                            messageTextLines.add(line)
                        }
                    }

                    val finalMessageText = messageTextLines.joinToString("\n").trim()

                    val aiMessage = ChatMessage(
                        id = "msg-${Clock.System.now().toEpochMilliseconds()}-ai",
                        sender = Sender.AI,
                        text = finalMessageText.ifBlank { responseText.trim() },
                        timestamp = Clock.System.now().toEpochMilliseconds(),
                        references = parsedReferences,
                        solutions = emptyList()
                    )

                    hujjahRepository.saveChatMessage(aiMessage)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = { error ->
                    val errorName = error::class.simpleName ?: ""
                    val isConnectionError = errorName.contains("Connect", ignoreCase = true) ||
                            errorName.contains("Host", ignoreCase = true) ||
                            errorName.contains("Socket", ignoreCase = true) ||
                            errorName.contains("Timeout", ignoreCase = true) ||
                            error.message?.contains("Connect", ignoreCase = true) == true ||
                            error.message?.contains("resolve host", ignoreCase = true) == true ||
                            error.message?.contains("Unable to resolve host", ignoreCase = true) == true

                    val errorMessageText = if (isConnectionError) {
                        "Koneksi internet terputus atau tidak stabil. Silakan periksa koneksi internet Anda dan coba lagi."
                    } else if (error.message?.contains("429") == true || error.message?.contains("quota", ignoreCase = true) == true) {
                        "Kunci API (API Key) Anda telah mencapai batas limit penggunaan (Quota Exceeded). API Key Anda valid, namun jatah gratisnya sudah habis atau dinonaktifkan oleh Google. Silakan buat API Key baru dengan akun Google lain atau periksa tagihan di Google AI Studio."
                    } else {
                        "Maaf, terjadi kesalahan saat menghubungi server AI. Mohon pastikan API Key Gemini yang Anda masukkan benar dan valid. Detail: ${error.message}"
                    }

                    val errorMsg = ChatMessage(
                        id = "msg-${Clock.System.now().toEpochMilliseconds()}-err",
                        sender = Sender.AI,
                        text = errorMessageText,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                    viewModelScope.launch { hujjahRepository.saveChatMessage(errorMsg) }
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            )
        }
    }


    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            hujjahRepository.deleteChatMessage(messageId)
        }
    }
}
