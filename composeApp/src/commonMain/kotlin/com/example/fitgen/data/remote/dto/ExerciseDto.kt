package com.example.fitgen.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDto(
    val bodyPart: String? = null,
    val equipment: String? = null,
    val gifUrl: String? = null,
    val id: String? = null,
    val name: String? = null,
    val target: String? = null,
    val secondaryMuscles: List<String>? = null,
    val instructions: List<String>? = null
)
