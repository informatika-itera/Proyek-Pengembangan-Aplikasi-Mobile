package com.example.fitgen.data.local.entity

import com.example.fitgen.data.local.ExerciseEntity
import com.example.fitgen.data.local.WorkoutLogEntity
import com.example.fitgen.domain.model.Exercise
import com.example.fitgen.domain.model.WorkoutLog
import kotlinx.datetime.LocalDate

// ==================== WorkoutLog Mapper ====================

/**
 * Mengonversi baris WorkoutLogEntity dari SQLDelight menjadi domain model WorkoutLog.
 * @param exercises daftar exercise yang sudah di-fetch terpisah berdasarkan workout_log_id
 */
fun WorkoutLogEntity.toDomain(exercises: List<Exercise> = emptyList()): WorkoutLog {
    return WorkoutLog(
        id = id,
        tanggal = LocalDate.fromEpochDays(tanggal.toInt()),
        gerakan = exercises,
        catatan = catatan
    )
}

fun List<WorkoutLogEntity>.toDomainList(
    exercisesMap: Map<Long, List<Exercise>> = emptyMap()
): List<WorkoutLog> {
    return map { entity ->
        entity.toDomain(exercises = exercisesMap[entity.id] ?: emptyList())
    }
}

// ==================== Exercise Mapper ====================

/**
 * Mengonversi baris ExerciseEntity dari SQLDelight menjadi domain model Exercise.
 */
fun ExerciseEntity.toDomain(): Exercise {
    return Exercise(
        nama = nama,
        sets = sets.toInt(),
        reps = reps.toInt(),
        beban = beban
    )
}

fun List<ExerciseEntity>.toDomainList(): List<Exercise> {
    return map { it.toDomain() }
}
