package com.example.bridgebit.domain.repository

import com.example.bridgebit.domain.model.QuizQuestion

interface AIRepository {
    suspend fun summarize(text: String): Result<String>
    suspend fun generateIdeas(topic: String): Result<List<String>>
    suspend fun improveWriting(text: String, style: WritingStyle = WritingStyle.NEUTRAL): Result<String>
    suspend fun translate(text: String, targetLanguage: String): Result<String>
    suspend fun chat(message: String): Result<String>
    suspend fun suggestTitle(content: String): Result<String>

    /**
     * Generate [count] quiz questions from the given [vocabularyList] string.
     *
     * This uses a dedicated JSON-mode Gemini call (responseMimeType = "application/json")
     * to guarantee the response is a parseable JSON array, fixing the bug where
     * the plain-text "|||" delimiter format only ever returned 1 question.
     *
     * @param count          Number of questions to generate.
     * @param vocabularyList Comma-separated vocabulary terms from the user's Phrase Vault.
     * @return               A [Result] wrapping exactly [count] [QuizQuestion] objects.
     */
    suspend fun generateQuiz(count: Int, vocabularyList: String): Result<List<QuizQuestion>>
}

enum class WritingStyle(val displayName: String, val prompt: String) {
    NEUTRAL("Netral", "Perbaiki tulisan dengan gaya netral"),
    FORMAL("Formal", "Perbaiki tulisan dengan gaya formal dan profesional"),
    CASUAL("Kasual", "Perbaiki tulisan dengan gaya santai dan friendly"),
    ACADEMIC("Akademik", "Perbaiki tulisan dengan gaya akademik dan ilmiah"),
    CREATIVE("Kreatif", "Perbaiki tulisan dengan gaya kreatif dan menarik")
}
