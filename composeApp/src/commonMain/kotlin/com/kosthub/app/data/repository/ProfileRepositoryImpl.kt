package com.kosthub.app.data.repository

import com.kosthub.app.data.local.KostDatabase
import com.kosthub.app.domain.model.Profile
import com.kosthub.app.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ProfileRepositoryImpl(private val database: KostDatabase) : ProfileRepository {
    private val queries = database.profileQueries

    override suspend fun getProfile(): Profile? = withContext(Dispatchers.Default) {
        queries.getProfile().executeAsOneOrNull()?.toDomain()
    }

    override suspend fun saveProfile(profile: Profile) = withContext(Dispatchers.Default) {
        queries.upsertProfile(
            id = profile.id,
            name = profile.name,
            email = profile.email,
            updated_at = currentTimeMillis()
        )
    }

    override suspend fun clearProfile() = withContext(Dispatchers.Default) {
        queries.clearProfile()
    }

    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}

private fun com.kosthub.app.data.local.ProfileEntity.toDomain(): Profile {
    return Profile(
        id = id,
        name = name,
        email = email
    )
}
