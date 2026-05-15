package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(idNumber: String): Result<User>
    suspend fun logout()
}
