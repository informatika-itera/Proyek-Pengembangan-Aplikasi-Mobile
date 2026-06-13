package com.example.fitgen.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.fitgen.data.local.FitGenDatabase
import com.example.fitgen.data.local.entity.toDomain
import com.example.fitgen.data.local.entity.toDomainList
import com.example.fitgen.domain.model.Exercise
import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class WorkoutRepositoryImpl(private val database: FitGenDatabase) : WorkoutRepository {

    private val queries = database.workoutQueries

    // ──────────────────────────────────────────────────────────────────────
    // Read operations (Flow — reaktif, auto-update saat data berubah)
    // ──────────────────────────────────────────────────────────────────────

    override fun getAllWorkouts(): Flow<List<WorkoutLog>> {
        return queries.getAllWorkoutLogs()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { logEntities ->
                logEntities.map { logEntity ->
                    val exercises = queries
                        .getExercisesForWorkoutLog(logEntity.id)
                        .executeAsList()
                        .toDomainList()
                    logEntity.toDomain(exercises)
                }
            }
    }

    override fun getWorkoutById(id: Long): Flow<WorkoutLog?> {
        return queries.getWorkoutLogById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { logEntity ->
                logEntity?.let {
                    val exercises = queries
                        .getExercisesForWorkoutLog(it.id)
                        .executeAsList()
                        .toDomainList()
                    it.toDomain(exercises)
                }
            }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Write operations (suspend — satu kali eksekusi di background thread)
    // ──────────────────────────────────────────────────────────────────────

    override suspend fun insertWorkout(workout: WorkoutLog): Long =
        withContext(Dispatchers.Default) {
            // 1. Simpan header WorkoutLog
            queries.insertWorkoutLog(
                tanggal = workout.tanggal.toEpochDays().toLong(),
                catatan = workout.catatan
            )
            val workoutLogId = queries.lastInsertWorkoutLogId().executeAsOne()

            // 2. Simpan setiap gerakan (Exercise) yang terkait
            workout.gerakan.forEach { exercise ->
                queries.insertExercise(
                    workout_log_id = workoutLogId,
                    nama           = exercise.nama,
                    sets           = exercise.sets.toLong(),
                    reps           = exercise.reps.toLong(),
                    beban          = exercise.beban
                )
            }

            workoutLogId
        }

    override suspend fun addExerciseToTodayLog(exercise: Exercise, catatanDurasi: String) =
        withContext(Dispatchers.Default) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
            val latestLog = queries.getLatestWorkoutLog().executeAsOneOrNull()

            if (latestLog != null && latestLog.tanggal == today) {
                // Log for today exists
                val exercises = queries.getExercisesForWorkoutLog(latestLog.id).executeAsList()
                val lastExercise = exercises.lastOrNull()

                if (lastExercise != null && lastExercise.nama == exercise.nama) {
                    // Consecutive same exercise -> update sets
                    queries.updateExerciseSets(
                        id = lastExercise.id,
                        sets = lastExercise.sets + exercise.sets.toLong()
                    )
                } else {
                    // Not consecutive or different exercise -> insert new exercise
                    queries.insertExercise(
                        workout_log_id = latestLog.id,
                        nama = exercise.nama,
                        sets = exercise.sets.toLong(),
                        reps = exercise.reps.toLong(),
                        beban = exercise.beban
                    )
                }

                // Append duration to catatan
                val newCatatan = if (latestLog.catatan.isBlank()) catatanDurasi else latestLog.catatan + "\n" + catatanDurasi
                queries.updateWorkoutLogCatatan(catatan = newCatatan, id = latestLog.id)
            } else {
                // Create new log for today
                queries.insertWorkoutLog(tanggal = today, catatan = catatanDurasi)
                val newLogId = queries.lastInsertWorkoutLogId().executeAsOne()
                queries.insertExercise(
                    workout_log_id = newLogId,
                    nama = exercise.nama,
                    sets = exercise.sets.toLong(),
                    reps = exercise.reps.toLong(),
                    beban = exercise.beban
                )
            }
        }

    override suspend fun updateWorkout(workout: WorkoutLog) =
        withContext(Dispatchers.Default) {
            // 1. Update header WorkoutLog
            queries.updateWorkoutLog(
                id      = workout.id,
                tanggal = workout.tanggal.toEpochDays().toLong(),
                catatan = workout.catatan
            )

            // 2. Hapus semua exercises lama lalu sisipkan ulang yang baru
            //    (replace strategy: sederhana dan aman untuk list yang berubah)
            queries.deleteExercisesByWorkoutLogId(workout.id)
            workout.gerakan.forEach { exercise ->
                queries.insertExercise(
                    workout_log_id = workout.id,
                    nama           = exercise.nama,
                    sets           = exercise.sets.toLong(),
                    reps           = exercise.reps.toLong(),
                    beban          = exercise.beban
                )
            }
        }

    override suspend fun deleteWorkout(id: Long) =
        withContext(Dispatchers.Default) {
            // ExerciseEntity akan terhapus otomatis via ON DELETE CASCADE
            queries.deleteWorkoutLogById(id)
        }
}
