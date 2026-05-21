package com.example.Roomie.data.repository

import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import io.github.aakira.napier.Napier

class SupabaseAuthRepositoryImpl(
    private val client: SupabaseClient,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override suspend fun login(idNumber: String): Result<User> {
        val trimmedId = idNumber.trim()
        return try {
            val email = "$trimmedId@roomie.itera.ac.id"
            val password = "pin_$trimmedId" 
            
            Napier.d("Supabase: Attempting login for $email")
            
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val session = client.auth.currentSessionOrNull()
            if (session != null) {
                Napier.d("Supabase: Login success for $email")
                val role = if (trimmedId.length <= 5) UserRole.ADMIN else UserRole.STUDENT
                val user = User(
                    id = session.user?.id ?: "",
                    name = trimmedId,
                    nim = trimmedId,
                    role = role
                )
                userPreferences.saveUser(user)
                Result.success(user)
            } else {
                Napier.e("Supabase: Session is null after sign in")
                Result.failure(Exception("Gagal membuat sesi login"))
            }
        } catch (e: Exception) {
            Napier.e("Supabase Login Error: ${e.message}", e)
            Result.failure(Exception("Login gagal: NIM tidak ditemukan atau PIN salah."))
        }
    }

    override fun getCurrentUser(): Flow<User?> = userPreferences.userData

    override suspend fun logout() {
        try {
            client.auth.signOut()
            userPreferences.clearUser()
        } catch (e: Exception) {
            Napier.e("Supabase Logout Error: ${e.message}", e)
            userPreferences.clearUser()
        }
    }
}
