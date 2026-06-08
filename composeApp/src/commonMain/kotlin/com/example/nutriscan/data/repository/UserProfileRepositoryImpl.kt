package com.example.nutriscan.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.nutriscan.data.local.NutriScanDatabase
import com.example.nutriscan.data.local.UserProfileEntity
import com.example.nutriscan.data.local.entity.UserProfileMapper.toDomain
import com.example.nutriscan.domain.model.Disease
import com.example.nutriscan.domain.model.UserProfile
import com.example.nutriscan.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import com.example.nutriscan.data.repository.ConsumptionRepositoryImpl
import com.example.nutriscan.domain.repository.ConsumptionRepository

class UserProfileRepositoryImpl(
    private val database: NutriScanDatabase
) : UserProfileRepository {

    // SQLDelight generates accessor from the .sq FILE name (UserProfile.sq → userProfileQueries)
    private val queries = database.userProfileQueries

    override fun getProfile(): Flow<UserProfile?> =
        queries.selectProfile()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity: UserProfileEntity? -> entity?.toDomain() }

    override suspend fun saveProfile(profile: UserProfile) =
        withContext(Dispatchers.Default) {
            val now = Clock.System.now().toEpochMilliseconds()
            queries.insertProfile(
                name       = profile.name,
                age        = profile.age.toLong(),
                weight     = profile.weight.toDouble(),
                height     = profile.height.toDouble(),
                conditions = Disease.toCsv(profile.healthConditions),
                created_at = now,
                updated_at = now
            )
        }

    override suspend fun updateProfile(profile: UserProfile) =
        withContext(Dispatchers.Default) {
            val now = Clock.System.now().toEpochMilliseconds()
            queries.updateProfile(
                name       = profile.name,
                age        = profile.age.toLong(),
                weight     = profile.weight.toDouble(),
                height     = profile.height.toDouble(),
                conditions = Disease.toCsv(profile.healthConditions),
                updated_at = now,
                id         = profile.id
            )
        }

    override suspend fun deleteProfile() =
        withContext(Dispatchers.Default) { queries.deleteProfile() }

    override suspend fun hasProfile(): Boolean =
        withContext(Dispatchers.Default) {
            queries.countProfiles().executeAsOne() > 0
        }
}