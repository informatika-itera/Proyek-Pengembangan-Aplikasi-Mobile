package com.example.inventra.domain.repository

import com.example.inventra.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    val isLoggedIn: Boolean

    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        email: String,
        password: String,
        name: String,
        division: String,
        role: String = "MEMBER"
    ): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun updateProfile(name: String, phone: String?, avatarUrl: String?): Result<User>
    suspend fun updateAvatar(imageBytes: ByteArray, fileName: String): Result<String>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun deleteUser(userId: String): Result<Unit>
    suspend fun updateUserRole(userId: String, role: String): Result<Unit>
}