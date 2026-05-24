package com.example.mapenumkm.domain.repository

import com.example.mapenumkm.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: User)
    suspend fun getUserByEmailOrPhone(identifier: String): User?
    suspend fun updatePassword(identifier: String, newPassword: String)
}
