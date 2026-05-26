package com.example.fitgen.domain.usecase

import com.example.fitgen.domain.repository.ExerciseImageRepository

class GetExerciseGifUseCase(
    private val repository: ExerciseImageRepository
) {
    /**
     * Executes the use case to find a GIF URL for the given exercise name.
     */
    suspend operator fun invoke(exerciseName: String): String? {
        if (exerciseName.isBlank()) return null
        return repository.getExerciseGifUrl(exerciseName)
    }
}
