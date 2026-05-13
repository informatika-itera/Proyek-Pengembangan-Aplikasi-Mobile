package com.example.fitgen.domain.usecase

import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Urutan pengurutan daftar workout log.
 */
enum class WorkoutSortBy(val displayName: String) {
    TANGGAL_TERBARU("Tanggal (Terbaru)"),
    TANGGAL_TERLAMA("Tanggal (Terlama)"),
    GERAKAN_TERBANYAK("Gerakan (Terbanyak)"),
    VOLUME_TERTINGGI("Volume Tertinggi")
}

/**
 * Use case untuk mengambil semua workout log dari repository.
 *
 * Business logic yang ditangani:
 * 1. Mengambil semua data dari repository secara reaktif (Flow)
 * 2. Menerapkan pengurutan sesuai preferensi pengguna
 *
 * @return Flow<List<WorkoutLog>> yang selalu up-to-date
 */
class GetAllWorkoutsUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(
        sortBy: WorkoutSortBy = WorkoutSortBy.TANGGAL_TERBARU
    ): Flow<List<WorkoutLog>> {
        return repository.getAllWorkouts().map { workouts ->
            when (sortBy) {
                WorkoutSortBy.TANGGAL_TERBARU    -> workouts.sortedByDescending { it.tanggal }
                WorkoutSortBy.TANGGAL_TERLAMA    -> workouts.sortedBy { it.tanggal }
                WorkoutSortBy.GERAKAN_TERBANYAK  -> workouts.sortedByDescending { it.jumlahGerakan }
                WorkoutSortBy.VOLUME_TERTINGGI   -> workouts.sortedByDescending { it.totalVolume }
            }
        }
    }
}
