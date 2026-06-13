package com.example.noteai.domain.repository

import com.example.noteai.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(user: User): Result<Unit>
    suspend fun logout()
}
