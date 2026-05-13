package com.example.fitgen.domain.repository

import com.example.fitgen.domain.model.WorkoutLog
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    /**
     * Mendapatkan semua history workout log dari database secara reaktif.
     * Daftar diurutkan berdasarkan tanggal terbaru.
     */
    fun getAllWorkouts(): Flow<List<WorkoutLog>>

    /**
     * Mendapatkan satu workout log berdasarkan ID secara reaktif.
     */
    fun getWorkoutById(id: Long): Flow<WorkoutLog?>

    /**
     * Menyimpan workout log baru beserta daftar gerakannya (exercises).
     * @return ID dari workout log yang baru saja disimpan.
     */
    suspend fun insertWorkout(workout: WorkoutLog): Long

    /**
     * Memperbarui workout log yang sudah ada beserta daftar gerakannya.
     * Akan menghapus daftar gerakan lama dan menggantinya dengan yang baru.
     */
    suspend fun updateWorkout(workout: WorkoutLog)

    /**
     * Menghapus workout log berdasarkan ID. 
     * Semua exercises yang terkait juga akan otomatis terhapus (Cascade Delete).
     */
    suspend fun deleteWorkout(id: Long)
}
