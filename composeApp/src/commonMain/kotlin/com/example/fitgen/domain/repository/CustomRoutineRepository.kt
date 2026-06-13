package com.example.fitgen.domain.repository

import com.example.fitgen.domain.model.CustomRoutine
import com.example.fitgen.domain.model.RoutineExercise
import kotlinx.coroutines.flow.Flow

interface CustomRoutineRepository {
    fun getAllRoutines(): Flow<List<CustomRoutine>>
    
    suspend fun insertRoutine(name: String): Long
    
    suspend fun deleteRoutine(id: Long)
    
    suspend fun addExerciseToRoutine(routineId: Long, exercise: RoutineExercise)
    
    suspend fun removeExerciseFromRoutine(exerciseId: Long)
}
