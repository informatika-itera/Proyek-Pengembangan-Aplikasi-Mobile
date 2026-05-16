package com.example.Roomie.data.repository

import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val userPreferences: UserPreferences
) : AuthRepository {
    
    override fun getCurrentUser(): Flow<User?> = userPreferences.userData

    override suspend fun login(idNumber: String): Result<User> {
        // Simulasi validasi role berdasarkan panjang karakter atau awalan
        // NIM Mahasiswa biasanya panjang (contoh: 123140019)
        // NIP Admin biasanya pendek atau beda format (kita asumsikan < 5 digit itu admin)
        
        return try {
            val user = if (idNumber.length <= 5) {
                User(
                    id = "ADMIN-1",
                    name = "Admin Sarpras",
                    nim = idNumber,
                    role = UserRole.ADMIN
                )
            } else {
                User(
                    id = "USER-$idNumber",
                    name = "Mulya Delani", // Default name for demo
                    nim = idNumber,
                    role = UserRole.STUDENT
                )
            }
            userPreferences.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        userPreferences.clearUser()
    }
}
