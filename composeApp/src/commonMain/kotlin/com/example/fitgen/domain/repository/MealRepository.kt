package com.example.fitgen.domain.repository

import com.example.fitgen.domain.model.MealLog
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    /** Mengambil semua meal log secara reaktif, diurutkan terbaru */
    fun getAllMeals(): Flow<List<MealLog>>

    /**
     * Mengambil semua meal log pada tanggal tertentu.
     * @param startOfDay  Timestamp Unix (milli) awal hari (00:00:00)
     * @param endOfDay    Timestamp Unix (milli) akhir hari (23:59:59)
     */
    fun getMealsByDate(startOfDay: Long, endOfDay: Long): Flow<List<MealLog>>

    /**
     * Menghitung total kalori yang dikonsumsi pada satu hari.
     * @return Total kalori (kkal), atau 0 jika belum ada log
     */
    suspend fun getDailyCaloriesTotal(startOfDay: Long, endOfDay: Long): Int

    /** Menyimpan meal log baru ke database */
    suspend fun insertMeal(meal: MealLog): Long

    /** Memperbarui meal log yang sudah ada */
    suspend fun updateMeal(meal: MealLog)

    /** Menghapus meal log berdasarkan ID */
    suspend fun deleteMeal(id: Long)
}