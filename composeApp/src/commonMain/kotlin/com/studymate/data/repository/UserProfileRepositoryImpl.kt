package com.studymate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.studymate.data.local.StudyMateDatabase
import com.studymate.data.local.UserProfileEntity
import com.studymate.domain.model.UserProfile
import com.studymate.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserProfileRepositoryImpl(
    private val database: StudyMateDatabase
) : UserProfileRepository {
    private val queries = database.userProfileQueries

    override fun getProfile(): Flow<UserProfile?> {
        return queries.getProfile()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override suspend fun saveProfile(profile: UserProfile) {
        withContext(Dispatchers.IO) {
            queries.insertProfile(
                name = profile.name,
                nim = profile.nim,
                currentStreak = profile.currentStreak.toLong(),
                lastStudyDate = profile.lastStudyDate
            )
        }
    }

    private fun UserProfileEntity.toDomain(): UserProfile {
        return UserProfile(
            id = id,
            name = name,
            nim = nim,
            currentStreak = currentStreak.toInt(),
            lastStudyDate = lastStudyDate
        )
    }
}
