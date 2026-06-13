package com.example.fitgen.domain.model

import com.example.fitgen.data.remote.dto.ExerciseDto

data class ChallengeDay(
    val dayNumber: Int,
    val isCompleted: Boolean = false,
    val exercises: List<ExerciseDto>
)

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val totalDays: Int,
    val imageId: String,
    val days: List<ChallengeDay>
)
