package com.example.fitgen.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.fitgen.data.local.FitGenDatabase
import com.example.fitgen.domain.model.CustomRoutine
import com.example.fitgen.domain.model.RoutineExercise
import com.example.fitgen.domain.repository.CustomRoutineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class CustomRoutineRepositoryImpl(
    db: FitGenDatabase
) : CustomRoutineRepository {
    
    private val queries = db.customRoutineQueries
    
    override fun getAllRoutines(): Flow<List<CustomRoutine>> {
        return queries.getAllRoutines()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    val exercises = queries.getExercisesForRoutine(entity.id).executeAsList().map { ex ->
                        RoutineExercise(
                            id = ex.id,
                            routineId = ex.routine_id,
                            name = ex.nama_gerakan,
                            bodyPart = ex.body_part,
                            gifUrl = ex.gif_url,
                            instructions = ex.instructions
                        )
                    }
                    
                    CustomRoutine(
                        id = entity.id,
                        name = entity.nama_rutinitas,
                        createdAt = entity.created_at,
                        exercises = exercises
                    )
                }
            }
    }

    override suspend fun insertRoutine(name: String): Long = withContext(Dispatchers.IO) {
        queries.transactionWithResult {
            queries.insertRoutine(
                nama_rutinitas = name,
                created_at = Clock.System.now().toEpochMilliseconds()
            )
            queries.lastInsertRoutineId().executeAsOne()
        }
    }

    override suspend fun deleteRoutine(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteRoutineById(id)
    }

    override suspend fun addExerciseToRoutine(routineId: Long, exercise: RoutineExercise) = withContext(Dispatchers.IO) {
        queries.insertRoutineExercise(
            routine_id = routineId,
            nama_gerakan = exercise.name,
            body_part = exercise.bodyPart,
            gif_url = exercise.gifUrl,
            instructions = exercise.instructions
        )
    }

    override suspend fun removeExerciseFromRoutine(exerciseId: Long) = withContext(Dispatchers.IO) {
        queries.deleteRoutineExerciseById(exerciseId)
    }
}
