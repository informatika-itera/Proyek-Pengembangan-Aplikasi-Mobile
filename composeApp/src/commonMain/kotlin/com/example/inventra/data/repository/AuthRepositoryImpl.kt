package com.example.inventra.data.repository

import com.example.inventra.core.network.SupabaseClientProvider
import com.example.inventra.data.remote.dto.ProfileDto
import com.example.inventra.domain.model.User
import com.example.inventra.domain.model.UserDivision
import com.example.inventra.domain.model.UserRole
import com.example.inventra.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepositoryImpl : AuthRepository {

    private val client = SupabaseClientProvider.client
    private val auth = client.auth
    private val db = client.postgrest

    override val currentUser: Flow<User?> = flow {
        emit(getCurrentUser())
    }

    override val isLoggedIn: Boolean
        get() = auth.currentSessionOrNull() != null

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = getCurrentUser()
                ?: return Result.failure(Exception("Gagal mengambil data user"))
            Result.success(user)
        } catch (e: Exception) {
            println("LOGIN ERROR: ${e.message}")
            Result.failure(Exception(parseAuthError(e.message)))
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        division: String,
        role: String
    ): Result<User> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("name", name)
                    put("role", role)
                    put("division", division)
                }
            }
            try {
                val allProfiles = db["profiles"].select().decodeList<ProfileDto>()
                val newProfile = allProfiles.find { it.name == name }
                if (newProfile == null) {
                    db["profiles"].upsert(
                        mapOf(
                            "name" to name,
                            "role" to role,
                            "division" to division
                        )
                    )
                }
            } catch (e: Exception) {
                println("Profile upsert: ${e.message}")
            }
            Result.success(
                User(
                    id = "",
                    name = name,
                    email = email,
                    role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.MEMBER },
                    division = try { UserDivision.valueOf(division) } catch (e: Exception) { UserDivision.PUBDOK }
                )
            )
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mendaftar: ${parseAuthError(e.message)}"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val authUser = auth.currentUserOrNull() ?: return null
            val profile = db["profiles"]
                .select(columns = Columns.ALL) {
                    filter { eq("id", authUser.id) }
                    limit(1)
                }
                .decodeSingleOrNull<ProfileDto>() ?: return null
            User(
                id = profile.id,
                name = profile.name,
                email = authUser.email ?: "",
                role = try { UserRole.valueOf(profile.role) } catch (e: Exception) { UserRole.MEMBER },
                division = try { UserDivision.valueOf(profile.division) } catch (e: Exception) { UserDivision.PUBDOK },
                studentId = profile.studentId,
                phone = profile.phone,
                avatarUrl = profile.avatarUrl,
                isActive = profile.isActive
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateProfile(name: String, phone: String?, avatarUrl: String?): Result<User> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Tidak terautentikasi"))
            val updateMap = mutableMapOf<String, Any?>("name" to name, "phone" to phone)
            if (avatarUrl != null) updateMap["avatar_url"] = avatarUrl
            db["profiles"].update(updateMap) {
                filter { eq("id", userId) }
            }
            val user = getCurrentUser()
                ?: return Result.failure(Exception("Gagal mengambil data"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAvatar(imageBytes: ByteArray, fileName: String): Result<String> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Tidak terautentikasi"))
            val bucket = client.storage["avatars"]
            val path = "$userId/$fileName"
            // Gunakan UploadData untuk kompatibilitas Supabase Kotlin SDK
            bucket.upload(path, imageBytes) { upsert = true }
            val publicUrl = bucket.publicUrl(path)
            db["profiles"].update(mapOf("avatar_url" to publicUrl)) {
                filter { eq("id", userId) }
            }
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal upload foto: ${e.message}"))
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val profiles = db["profiles"]
                .select(columns = Columns.ALL)
                .decodeList<ProfileDto>()
            val users = profiles.map { profile ->
                User(
                    id = profile.id,
                    name = profile.name,
                    email = "",
                    role = try { UserRole.valueOf(profile.role) } catch (e: Exception) { UserRole.MEMBER },
                    division = try { UserDivision.valueOf(profile.division) } catch (e: Exception) { UserDivision.PUBDOK },
                    studentId = profile.studentId,
                    phone = profile.phone,
                    avatarUrl = profile.avatarUrl,
                    isActive = profile.isActive
                )
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            db["profiles"].delete {
                filter { eq("id", userId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserRole(userId: String, role: String): Result<Unit> {
        return try {
            db["profiles"].update(mapOf("role" to role)) {
                filter { eq("id", userId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseAuthError(message: String?): String {
        return when {
            message == null -> "Terjadi kesalahan"
            message.contains("Invalid login credentials") -> "Email atau password salah"
            message.contains("Email not confirmed") -> "Email belum dikonfirmasi"
            message.contains("User already registered") -> "Email sudah terdaftar"
            message.contains("Password should be") -> "Password minimal 6 karakter"
            else -> message
        }
    }
}