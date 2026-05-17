package com.studymate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: Long = 1,
    val name: String,
    val nim: String,
    val currentStreak: Int = 0,
    val lastStudyDate: Long? = null
)
