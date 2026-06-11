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
        role: String = "MEMBER",
        studentId: String? = null
    ): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun updateProfile(
        name: String, 
        phone: String?, 
        avatarUrl: String?,
        divisionHead: String? = null,
        staffList: String? = null,
        studentId: String? = null
    ): Result<User>
    suspend fun updateAvatar(imageBytes: ByteArray, fileName: String): Result<String>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun getUserById(userId: String): Result<User>
    suspend fun deleteUser(userId: String): Result<Unit>
    suspend fun updateUserRole(userId: String, role: String): Result<Unit>

    /** Admin: edit nama user lain untuk pengelolaan divisi */
    suspend fun updateUserName(userId: String, name: String): Result<Unit>

    /** Admin: edit student id user lain */
    suspend fun updateUserStudentId(userId: String, studentId: String?): Result<Unit>

    /** Admin: edit email user lain */
    suspend fun updateUserEmail(userId: String, email: String): Result<Unit>

    /** Admin: edit password user lain */
    suspend fun updateUserPassword(userId: String, password: String): Result<Unit>
}