package com.example.fitgen.domain.usecase

import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.repository.WorkoutRepository

/**
 * Use case untuk menyimpan satu sesi workout log baru.
 *
 * Business logic yang ditangani:
 * 1. Validasi: workout harus memiliki minimal 1 gerakan
 * 2. Validasi: setiap gerakan harus valid (nama tidak kosong, sets/reps > 0, beban >= 0)
 * 3. Jika semua valid → delegasikan ke repository untuk disimpan
 *
 * @return Result.success(id) jika berhasil, Result.failure(exception) jika gagal
 */
class LogWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(workout: WorkoutLog): Result<Long> {
        return try {
            // Validasi 1: harus ada minimal 1 gerakan
            if (workout.gerakan.isEmpty()) {
                return Result.failure(
                    IllegalArgumentException("Sesi latihan harus memiliki minimal 1 gerakan")
                )
            }

            // Validasi 2: setiap gerakan harus valid
            val gerakanTidakValid = workout.gerakan.filter { !it.isValid }
            if (gerakanTidakValid.isNotEmpty()) {
                val namaGerakan = gerakanTidakValid
                    .mapIndexed { i, e ->
                        if (e.nama.isBlank()) "Gerakan #${i + 1}" else e.nama
                    }
                    .joinToString(", ")
                return Result.failure(
                    IllegalArgumentException(
                        "Data gerakan tidak valid: $namaGerakan. " +
                        "Pastikan nama tidak kosong, sets/reps > 0, dan beban >= 0"
                    )
                )
            }

            // Delegasi ke repository
            val id = if (workout.id == 0L) {
                repository.insertWorkout(workout)
            } else {
                repository.updateWorkout(workout)
                workout.id
            }

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
