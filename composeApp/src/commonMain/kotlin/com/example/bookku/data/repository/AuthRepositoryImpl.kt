package com.example.bookku.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.bookku.data.local.BookDatabase
import com.example.bookku.data.local.datastore.UserPreferences
import com.example.bookku.data.local.entity.toDomain
import com.example.bookku.domain.model.User
import com.example.bookku.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.random.Random

class AuthRepositoryImpl(
    private val database: BookDatabase,
    private val userPreferences: UserPreferences
) : AuthRepository {
    private val queries = database.userQueries

    override val currentUser: Flow<User?> = userPreferences.userId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else queries.getUserById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        val entity = queries.getUserByEmail(email).executeAsOneOrNull()
        return if (entity != null && entity.password == password) {
            val user = entity.toDomain()
            userPreferences.saveUserSession(user.id, user.username)
            Result.success(user)
        } else {
            Result.failure(Exception("Email atau password salah"))
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        val existing = queries.getUserByEmail(email).executeAsOneOrNull()
        if (existing != null) return Result.failure(Exception("Email sudah terdaftar"))

        val id = "user_${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(1000)}"
        val createdAt = Clock.System.now().toEpochMilliseconds()
        
        queries.insertUser(id, username, email, password, createdAt)
        
        val user = User(id, username, email, "", "", Instant.fromEpochMilliseconds(createdAt))
        userPreferences.saveUserSession(id, username)
        return Result.success(user)
    }

    override suspend fun logout() {
        userPreferences.clearUserSession()
    }

    override suspend fun getCurrentUserId(): String? {
        return userPreferences.userId.first()
    }
}
