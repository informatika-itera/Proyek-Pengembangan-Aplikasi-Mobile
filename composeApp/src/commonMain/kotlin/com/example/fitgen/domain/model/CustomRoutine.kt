package com.example.fitgen.domain.model

data class CustomRoutine(
    val id: Long = 0,
    val name: String,
    val createdAt: Long,
    val exercises: List<RoutineExercise> = emptyList()
)

data class RoutineExercise(
    val id: Long = 0,
    val routineId: Long = 0,
    val name: String,
    val bodyPart: String,
    val gifUrl: String,
    val instructions: String
)
