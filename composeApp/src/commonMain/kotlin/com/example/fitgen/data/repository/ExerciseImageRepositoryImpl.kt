package com.example.fitgen.data.repository

import com.example.fitgen.core.network.NetworkResult
import com.example.fitgen.data.remote.api.ExerciseApiService
import com.example.fitgen.domain.repository.ExerciseImageRepository

class ExerciseImageRepositoryImpl(
    private val apiService: ExerciseApiService
) : ExerciseImageRepository {
    override suspend fun getExerciseGifUrl(exerciseName: String): String? {
        // Membersihkan nama gerakan untuk meningkatkan kecocokan API (misal: hapus angka atau kata "Set")
        val cleanName = exerciseName.lowercase().trim()
        
        return when (val result = apiService.searchExerciseByName(cleanName)) {
            is NetworkResult.Success -> {
                // Ambil hasil pertama yang paling cocok
                result.data.firstOrNull()?.gifUrl
            }
            is NetworkResult.Error -> {
                // Jika error, return null (misal karena limit API atau tidak ketemu)
                null
            }
            is NetworkResult.Loading -> null
        }
    }
}
