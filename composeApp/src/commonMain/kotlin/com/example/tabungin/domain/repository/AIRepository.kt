package com.example.tabungin.domain.repository

interface AIRepository {
    suspend fun summarize(text: String): Result<String>
    suspend fun generateIdeas(topic: String): Result<List<String>>
    suspend fun improveWriting(text: String, style: WritingStyle = WritingStyle.NEUTRAL): Result<String>
    suspend fun translate(text: String, targetLanguage: String): Result<String>
    suspend fun chat(message: String, isTabunganContext: Boolean = false): Result<String>
    suspend fun suggestTitle(content: String): Result<String>
}

enum class WritingStyle(val displayName: String, val prompt: String) {
    NEUTRAL("Netral", "Perbaiki catatan tabungan dengan bahasa netral dan straightforward"),
    FORMAL("Formal", "Perbaiki memo keuangan dengan bahasa formal dan sopan"),
    CASUAL("Kasual", "Perbaiki catatan menabung dengan bahasa santai dan friendly"),
    ACADEMIC("Akademik", "Perbaiki laporan tabungan dengan bahasa akademis"),
    CREATIVE("Kreatif", "Perbaiki motivator tabungan dengan bahasa kreatif dan menarik")
}
