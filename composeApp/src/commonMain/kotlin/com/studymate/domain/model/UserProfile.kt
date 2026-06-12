package com.studymate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: Long = 1,
    val email: String? = null,
    val googleName: String? = null,
    val googlePhotoUrl: String? = null,
    val localName: String? = null,
    val localPhotoPath: String? = null,
    val nim: String = "",
    val major: String = "",
    val lifeGoals: String = "",
    val currentStreak: Int = 0,
    val lastStudyDate: Long? = null,
    val dailyMantra: String = "Jangan pernah berhenti belajar, karena hidup tidak pernah berhenti mengajar."
) {
    val displayName: String
        get() = if (!localName.isNullOrBlank()) localName 
                else if (!googleName.isNullOrBlank()) googleName 
                else "User StudyMate"
        
    val displayPhoto: String?
        get() = if (!localPhotoPath.isNullOrBlank()) localPhotoPath 
                else if (!googlePhotoUrl.isNullOrBlank()) googlePhotoUrl 
                else null
}

@Serializable
data class ActivityDay(
    val date: String,
    val quizCount: Int,
    val notesCount: Int,
    val totalPoints: Int
)

enum class AchievementTier {
    NONE, BRONZE, SILVER, GOLD
}
