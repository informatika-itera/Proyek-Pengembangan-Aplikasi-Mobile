package com.studymate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.studymate.data.local.StudyMateDatabase
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

    override fun getProfile(): Flow<UserProfile?> {
        return database.userProfileQueries.getProfile()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    UserProfile(
                        id = it.id,
                        email = it.email,
                        googleName = it.googleName,
                        googlePhotoUrl = it.googlePhotoUrl,
                        localName = it.localName,
                        localPhotoPath = it.localPhotoPath,
                        nim = it.nim,
                        major = it.major,
                        currentStreak = it.currentStreak.toInt(),
                        lastStudyDate = it.lastStudyDate,
                        dailyMantra = it.dailyMantra
                    )
                }
            }
    }

    override suspend fun saveProfile(profile: UserProfile) {
        withContext(Dispatchers.IO) {
            database.userProfileQueries.insertProfile(
                email = profile.email,
                googleName = profile.googleName,
                googlePhotoUrl = profile.googlePhotoUrl,
                localName = profile.localName,
                localPhotoPath = profile.localPhotoPath,
                nim = profile.nim,
                major = profile.major,
                currentStreak = profile.currentStreak.toLong(),
                lastStudyDate = profile.lastStudyDate,
                dailyMantra = profile.dailyMantra
            )
        }
    }

    override suspend fun updateLocalProfile(name: String?, photoPath: String?, nim: String?, major: String?) {
        withContext(Dispatchers.IO) {
            database.userProfileQueries.updateLocalProfile(
                localName = name,
                localPhotoPath = photoPath,
                nim = nim ?: "",
                major = major ?: ""
            )
        }
    }

    override suspend fun updateStreak(streak: Int, lastStudyDate: Long?) {
        withContext(Dispatchers.IO) {
            database.userProfileQueries.updateStreak(streak.toLong(), lastStudyDate)
        }
    }

    override suspend fun updateMantra(mantra: String) {
        withContext(Dispatchers.IO) {
            database.userProfileQueries.updateMantra(mantra)
        }
    }
}
