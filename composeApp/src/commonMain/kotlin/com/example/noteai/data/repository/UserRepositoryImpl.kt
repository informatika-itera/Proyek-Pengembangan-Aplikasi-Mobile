package com.example.noteai.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.noteai.data.local.NoteDatabase
import com.example.noteai.domain.model.User
import com.example.noteai.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val database: NoteDatabase
) : UserRepository {
    private val queries = database.noteQueries

    override fun getCurrentUser(): Flow<User?> {
        return queries.getCurrentUser()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    User(it.id, it.email, it.name, it.password, it.is_logged_in == 1L)
                }
            }
    }

    override suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        val userEntity = queries.getUserByEmail(email).executeAsOneOrNull()
        if (userEntity != null && userEntity.password == password) {
            queries.logoutAllUsers()
            queries.loginUser(email, password)
            Result.success(User(userEntity.id, userEntity.email, userEntity.name, userEntity.password, true))
        } else {
            Result.failure(Exception("Email atau password salah"))
        }
    }

    override suspend fun register(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            queries.insertUser(user.email, user.name, user.password)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        queries.logoutAllUsers()
    }
}
