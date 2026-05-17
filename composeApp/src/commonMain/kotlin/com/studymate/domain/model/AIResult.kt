package com.studymate.domain.model

sealed class AIResult {
    data class RefineSuccess(val refinedText: String) : AIResult()
    data class QuizSuccess(val questionsJson: String) : AIResult()
    data class Failure(val error: String) : AIResult()
    object Loading : AIResult()
}
