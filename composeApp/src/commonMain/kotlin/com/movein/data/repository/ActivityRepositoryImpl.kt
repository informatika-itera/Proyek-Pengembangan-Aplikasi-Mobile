package com.movein.data.repository

import com.movein.data.remote.api.GeminiApiService
import com.movein.domain.model.ActivityModel
import com.movein.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock

class ActivityRepositoryImpl(
    private val geminiApiService: GeminiApiService
) : ActivityRepository {

    // Simulasi database lokal sementara menggunakan list memori agar proyekmu bisa di-run dulu tanpa error
    private val localDatabaseSimulated = MutableStateFlow<List<ActivityModel>>(emptyList())

    override fun getAllActivities(): Flow<List<ActivityModel>> {
        return localDatabaseSimulated
    }

    override suspend fun generateAndSaveActivity(mood: String): ActivityModel {
        // 1. Ambil rekomendasi teks dari AI Gemini lewat Ktor Client
        val response = geminiApiService.generateActivitySuggestion(mood)
        val rawText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "Gagal mendapatkan rekomendasi aktivitas dari Gemini AI."

        // Memisahkan baris teks respon dari Gemini menjadi Judul dan Deskripsi
        val lines = rawText.lines().filter { it.isNotBlank() }
        val title = lines.firstOrNull()?.replace("**", "") ?: "Aktivitas untuk Mood $mood"
        val description = lines.drop(1).joinToString("\n").replace("**", "")
            .ifBlank { "Lakukan aktivitas positif ini agar mood kamu jadi lebih baik!" }

        // 2. Bungkus menjadi objek data class ActivityModel
        val newActivity = ActivityModel(
            id = Clock.System.now().toEpochMilliseconds(),
            title = title,
            description = description,
            mood = mood,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        // 3. Simpan langsung ke memori lokal (Simulasi Cache-First Strategy)
        val currentList = localDatabaseSimulated.value.toMutableList()
        currentList.add(0, newActivity) // Selalu masukkan aktivitas terbaru di posisi paling atas
        localDatabaseSimulated.value = currentList

        return newActivity
    }
}