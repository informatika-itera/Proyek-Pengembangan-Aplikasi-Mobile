package com.example.bridgebit.data.repository

import com.example.bridgebit.data.remote.api.GeminiService
import com.example.bridgebit.data.remote.api.SystemPrompts
import com.example.bridgebit.data.remote.dto.QuizQuestionDto
import com.example.bridgebit.data.remote.dto.toDomain
import com.example.bridgebit.domain.model.QuizQuestion
import com.example.bridgebit.domain.repository.AIRepository
import com.example.bridgebit.domain.repository.WritingStyle
import kotlinx.serialization.json.Json

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    /**
     * Lenient Json parser — ignores unknown keys so minor API schema changes
     * don't break deserialization.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    override suspend fun summarize(text: String): Result<String> {
        val prompt = """
            Rangkum teks berikut:
            
            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.SUMMARIZER
        )
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        val prompt = """
            Berikan 5 ide kreatif untuk topik: $topic
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.IDEA_GENERATOR
        ).map { response ->
            response.lines()
                .filter { it.isNotBlank() }
                .map { line ->
                    line.replace(Regex("^\\d+\\.\\s*"), "").trim()
                }
                .filter { it.isNotBlank() }
        }
    }

    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        val styleInstruction = when (style) {
            WritingStyle.FORMAL -> "Gunakan gaya formal dan profesional."
            WritingStyle.CASUAL -> "Gunakan gaya santai dan friendly."
            WritingStyle.ACADEMIC -> "Gunakan gaya akademik dan ilmiah."
            WritingStyle.CREATIVE -> "Gunakan gaya kreatif dan menarik."
            WritingStyle.NEUTRAL -> "Gunakan gaya netral."
        }

        val prompt = """
            $styleInstruction
            
            Perbaiki tulisan berikut:
            
            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.WRITING_IMPROVER
        )
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val prompt = """
            Terjemahkan ke bahasa $targetLanguage:
            
            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }

    override suspend fun chat(message: String): Result<String> {
        return geminiService.generateContent(prompt = message)
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        val prompt = """
            Berikan saran judul untuk konten berikut:
            
            $content
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TITLE_SUGGESTER
        ).map { it.trim().removeSurrounding("\"") }
    }

    /**
     * Generates quiz questions using Gemini's JSON mode.
     *
     * ## Why This Fixes the "Only 1 Question" Bug
     *
     * The previous implementation used a plain-text prompt with `"|||"` delimiters.
     * Gemini 2.5-Flash would often:
     *   1. Wrap the entire response in a markdown code fence (``` ```json ... ``` ```),
     *      causing `split("|||")` to return a single unsplit block.
     *   2. Vary the delimiter format (extra spaces, newlines) causing missed splits.
     *
     * By calling [GeminiService.generateStructuredContent] with
     * `responseMimeType = "application/json"`, the Gemini API **guarantees** the
     * response is a raw JSON string — no markdown, no prose. We then directly
     * deserialize it as `List<QuizQuestionDto>` using `kotlinx.serialization`.
     *
     * @param count          Exact number of questions requested.
     * @param vocabularyList Comma-separated vocab terms to build questions from.
     */
    override suspend fun generateQuiz(
        count: Int,
        vocabularyList: String
    ): Result<List<QuizQuestion>> {
        // The prompt explicitly states the schema and the exact count.
        // Temperature is kept low in GeminiService.generateStructuredContent (0.4)
        // to maximise structural consistency of the JSON array.
        val prompt = """
            Create EXACTLY $count vocabulary quiz questions based on these terms: $vocabularyList.
            
            Return a JSON array with EXACTLY $count objects. Each object must follow this schema:
            {
              "question": "<quiz question text in Indonesian>",
              "options": ["<option 1>", "<option 2>", "<option 3>", "<option 4>"],
              "answer": "<the correct option text, must exactly match one of the options>",
              "explanation": "<brief explanation in Indonesian>"
            }
            
            Requirements:
            - Questions must test vocabulary understanding or translation.
            - All 4 options must be plausible but only 1 correct.
            - The "answer" value must be the full text of one of the "options".
            - Write questions and explanations in Indonesian.
            - Return ONLY the JSON array. No other text.
        """.trimIndent()

        return geminiService.generateStructuredContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.QUIZ_GENERATOR,
            responseMimeType = "application/json",
            maxOutputTokens = count * 400  // ~400 tokens per question is generous headroom
        ).mapCatching { rawJson ->
            // Strip any residual markdown code fences as a safety net
            val cleanJson = rawJson
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val dtos = json.decodeFromString<List<QuizQuestionDto>>(cleanJson)

            if (dtos.isEmpty()) {
                throw Exception("AI returned an empty quiz list. Please try again.")
            }

            // take(count) ensures we never return more than requested;
            // if AI returned fewer, we surface all of them rather than error.
            dtos.take(count).map { it.toDomain() }
        }
    }
}
