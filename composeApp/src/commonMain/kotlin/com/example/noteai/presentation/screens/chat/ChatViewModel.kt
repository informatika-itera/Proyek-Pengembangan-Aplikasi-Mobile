package com.example.noteai.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.Recipe
import com.example.noteai.domain.repository.AIRepository
import com.example.noteai.domain.repository.PantryRepository
import com.example.noteai.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val canBeSavedAsRecipe: Boolean = false // Tambahan penanda jika pesan ini resep
)

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("Halo! Mau masak apa hari ini?", false)
    ),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null // Untuk notifikasi jika berhasil disimpan
)

class ChatViewModel(
    private val aiRepository: AIRepository,
    private val pantryRepository: PantryRepository, // Tambahkan ini
    private val recipeRepository: RecipeRepository  // Tambahkan ini
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun sendMessage() {
        val messageText = _uiState.value.inputText
        if (messageText.isBlank()) return

        val userMessage = ChatMessage(messageText, true)
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                // 1. Ambil data bahan di pantry
                val pantryList = pantryRepository.getAllPantryItems().first()
                val pantryContext = if (pantryList.isNotEmpty()) {
                    pantryList.joinToString(", ") { "${it.name} (${it.amount} ${it.unit})" }
                } else {
                    "Pantry sedang kosong."
                }

                // 2. Buat Prompt Terstruktur dengan menyisipkan Konteks Pantry
                val enrichedPrompt = """
                    Pesan Pengguna: $messageText
                    
                    INFORMASI SISTEM:
                    Bahan-bahan yang tersedia di pantry pengguna saat ini: $pantryContext.
                    
                    TUGAS:
                    Jika pengguna meminta resep, utamakan membuat resep menggunakan bahan-bahan di atas. 
                    Jika kamu memberikan resep utuh, WAJIB gunakan format persis seperti di bawah ini agar sistem bisa menyimpannya:
                    JUDUL: [Judul Resep]
                    BAHAN: [Daftar Bahan]
                    INSTRUKSI: [Langkah-langkah memasak]
                """.trimIndent()

                // Kirim prompt yang sudah diperkaya ke AI
                val result = aiRepository.chat(enrichedPrompt)

                result.onSuccess { aiResponse ->
                    // Cek apakah balasan AI mengandung format resep yang kita minta
                    val isRecipe = aiResponse.contains("JUDUL:") && aiResponse.contains("BAHAN:")

                    _uiState.update {
                        it.copy(
                            messages = it.messages + ChatMessage(aiResponse, false, isRecipe),
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            messages = it.messages + ChatMessage("Maaf, terjadi kesalahan: ${error.message}", false),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // 3. Fungsi untuk mengekstrak format AI dan memasukkannya ke List Resep
    // Fungsi untuk mengekstrak format AI dan memasukkannya ke List Resep
    fun saveAiRecipe(messageText: String) {
        viewModelScope.launch {
            try {
                // Pastikan teks mengandung semua kata kunci yang dibutuhkan
                if (messageText.contains("JUDUL:") &&
                    messageText.contains("BAHAN:") &&
                    messageText.contains("INSTRUKSI:")
                ) {
                    // Ekstrak teks menggunakan fungsi pemotong String bawaan Kotlin
                    val title = messageText
                        .substringAfter("JUDUL:")
                        .substringBefore("BAHAN:")
                        .trim()

                    val ingredients = messageText
                        .substringAfter("BAHAN:")
                        .substringBefore("INSTRUKSI:")
                        .trim()

                    val instructions = messageText
                        .substringAfter("INSTRUKSI:")
                        .trim()

                    // Pastikan hasil ekstraksi tidak kosong
                    if (title.isNotEmpty() && ingredients.isNotEmpty() && instructions.isNotEmpty()) {
                        val recipe = Recipe(
                            title = title,
                            ingredients = ingredients,
                            instructions = instructions,
                            isAiGenerated = true // Tandai sebagai buatan AI
                        )

                        // Simpan ke database
                        recipeRepository.insertRecipe(recipe)

                        _uiState.update { it.copy(snackbarMessage = "Resep berhasil disimpan!") }
                    } else {
                        _uiState.update { it.copy(snackbarMessage = "Gagal membaca detail resep") }
                    }
                } else {
                    _uiState.update { it.copy(snackbarMessage = "Format resep tidak sesuai") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Error saat menyimpan resep: ${e.message}") }
            }
        }
    }
}