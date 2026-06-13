package com.example.fitgen.data.repository

import com.example.fitgen.data.remote.api.ExerciseApiService
import com.example.fitgen.domain.repository.ExerciseImageRepository

class ExerciseImageRepositoryImpl(
    private val apiService: ExerciseApiService
) : ExerciseImageRepository {
    override suspend fun getExerciseGifUrl(exerciseName: String): String? {
        val formatted = exerciseName.trim().split(" ").joinToString("_") { it.replaceFirstChar { c -> c.uppercase() } }
        val baseUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/$formatted"
        return "$baseUrl/0.jpg,$baseUrl/1.jpg"
    }
}
