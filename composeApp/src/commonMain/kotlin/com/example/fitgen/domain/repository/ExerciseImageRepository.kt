package com.example.fitgen.domain.repository

interface ExerciseImageRepository {
    /**
     * Finds the GIF URL for a given exercise name.
     * Returns null if not found or on error.
     */
    suspend fun getExerciseGifUrl(exerciseName: String): String?
}
