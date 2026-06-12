package com.example.bridgebit.data.remote.dto

import com.example.bridgebit.domain.model.QuizQuestion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for a single quiz question returned by the Gemini API
 * when [GenerationConfig.responseMimeType] is set to "application/json".
 *
 * Gemini is instructed (via [com.example.bridgebit.data.remote.api.SystemPrompts.QUIZ_GENERATOR])
 * to respond with a JSON array of objects matching this schema:
 *
 * ```json
 * [
 *   {
 *     "question": "What does 'Blockchain' mean in Indonesian?",
 *     "options": ["Rantai Blok", "Koin Digital", "Server", "Dompet"],
 *     "answer": "Rantai Blok",
 *     "explanation": "Blockchain translates to Rantai Blok."
 *   }
 * ]
 * ```
 *
 * The [answer] field is the *text* of the correct option (not an index),
 * so [toDomain] resolves the index by finding the matching string in [options].
 */
@Serializable
data class QuizQuestionDto(
    @SerialName("question")
    val question: String,

    @SerialName("options")
    val options: List<String>,

    /**
     * The correct answer as a plain string matching one of the [options].
     * Using a string value instead of an index makes the prompt simpler and
     * less error-prone — the AI doesn't have to count option positions.
     */
    @SerialName("answer")
    val answer: String,

    @SerialName("explanation")
    val explanation: String
)

/**
 * Maps a [QuizQuestionDto] to the domain [QuizQuestion] model.
 *
 * [correctOptionIndex] is derived by finding the position of [QuizQuestionDto.answer]
 * inside [QuizQuestionDto.options]. Defaults to 0 if the answer string is not found
 * (guards against minor AI hallucinations where the answer text differs slightly).
 */
fun QuizQuestionDto.toDomain(): QuizQuestion {
    val correctIndex = options.indexOfFirst { it.equals(answer, ignoreCase = true) }
        .takeIf { it >= 0 } ?: 0

    return QuizQuestion(
        question = question,
        options = options,
        correctOptionIndex = correctIndex,
        explanation = explanation
    )
}
