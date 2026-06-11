package com.example.inventra.data.repository

import com.example.inventra.core.network.ApiConfig
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
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.isSuccess
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : AuthRepository {

    private val client = SupabaseClientProvider.client
    private val adminClient = SupabaseClientProvider.adminClient
    private val auth = client.auth
    private val db = client.postgrest
    private val adminDb = adminClient.postgrest
    private val json = Json { ignoreUnknownKeys = true }

    override val currentUser: Flow<User?> = flow { emit(getCurrentUser()) }

    override val isLoggedIn: Boolean
        get() = auth.currentSessionOrNull() != null

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            auth.signInWith(Email) { this.email = email; this.password = password }
            val user = getCurrentUserOrCreate()
                ?: return Result.failure(
                    Exception("Gagal mengambil data user. Jalankan SQL fix di Supabase SQL Editor.")
                )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(parseAuthError(e.message)))
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        division: String,
        role: String,
        studentId: String?
    ): Result<User> {
        return try {
            val bodyJson = """
                {
                  "email": "${email.trim()}",
                  "password": "$password",
                  "email_confirm": true,
                  "user_metadata": {
                    "name": "$name",
                    "division": "$division",
                    "role": "$role",
                    "student_id": "${studentId ?: ""}"
                  }
                }
            """.trimIndent()

            val response = httpClient.post("${ApiConfig.supabaseUrl}/auth/v1/admin/users") {
                header("apikey", ApiConfig.supabaseServiceRoleKey)
                header("Authorization", "Bearer ${ApiConfig.supabaseServiceRoleKey}")
                setBody(TextContent(bodyJson, ContentType.Application.Json))
            }

            val responseText = response.bodyAsText()

            if (!response.status.isSuccess()) {
                val serverMsg = runCatching {
                    json.parseToJsonElement(responseText)
                        .jsonObject["msg"]?.jsonPrimitive?.content
                        ?: json.parseToJsonElement(responseText)
                            .jsonObject["message"]?.jsonPrimitive?.content
                }.getOrNull() ?: "HTTP ${response.status.value}"
                return Result.failure(Exception(parseAuthError(serverMsg)))
            }

            val newUserId = runCatching {
                json.parseToJsonElement(responseText)
                    .jsonObject["id"]?.jsonPrimitive?.content
            }.getOrNull()
                ?: return Result.failure(Exception("Gagal mendapat user ID dari respons"))

            kotlinx.coroutines.delay(700)

            try {
                adminDb["profiles"].upsert(
                    buildJsonObject {
                        put("id", newUserId)
                        put("name", name)
                        put("role", role)
                        put("division", division)
                        if (studentId != null) put("student_id", studentId)
                        put("is_active", true)
                    }
                )
            } catch (e: Exception) {
                // REGISTER profile upsert note
            }

            Result.success(
                User(
                    id = newUserId, name = name, email = email.trim(),
                    role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.MEMBER },
                    division = try {
                        UserDivision.valueOf(division)
                    } catch (e: Exception) { UserDivision.PUBDOK },
                    studentId = studentId
                )
            )
        } catch (e: Exception) {
            Result.failure(Exception(parseAuthError(e.message)))
        }
    }

    override suspend fun logout(): Result<Unit> = try {
        auth.signOut(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getCurrentUser(): User? {
        return try {
            val authUser = auth.currentUserOrNull() ?: return null
            db["profiles"]
                .select(columns = Columns.ALL) {
                    filter { eq("id", authUser.id) }
                    limit(1)
                }.decodeSingleOrNull<ProfileDto>()
                ?.toUser(authUser.email ?: "")
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getCurrentUserOrCreate(): User? {
        return try {
            val authUser = auth.currentUserOrNull() ?: return null

            // Coba baca profile
            val existingProfile = try {
                db["profiles"]
                    .select(columns = Columns.ALL) {
                        filter { eq("id", authUser.id) }
                        limit(1)
                    }.decodeSingleOrNull<ProfileDto>()
            } catch (e: Exception) {
                null
            }

            if (existingProfile != null) {
                return existingProfile.toUser(authUser.email ?: "")
            }

            // Profile tidak ada → buat otomatis

            val emailStr = authUser.email ?: ""
            val defaultName = emailStr.substringBefore("@")
                .replace(".", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                .ifBlank { "User" }

            val isBendaharaEmail = emailStr.contains("bendahara", ignoreCase = true) || 
                                  emailStr.contains("admin", ignoreCase = true)
            
            // Sprint 4 fix: Bendahara Umum otomatis ADMIN, divisi lain otomatis MEMBER
            val defaultDivision = if (isBendaharaEmail) "BENDAHARA_UMUM" else "PUBDOK"
            val defaultRole = if (defaultDivision == "BENDAHARA_UMUM") "ADMIN" else "MEMBER"

            try {
                adminDb["profiles"].upsert(
                    buildJsonObject {
                        put("id", authUser.id)
                        put("name", defaultName)
                        put("role", defaultRole)
                        put("division", defaultDivision)
                        put("is_active", true)
                    }
                )
            } catch (e: Exception) {
                // LOGIN: gagal buat profile
            }

            // Baca ulang atau kembalikan fallback
            val newProfile = try {
                db["profiles"]
                    .select(columns = Columns.ALL) {
                        filter { eq("id", authUser.id) }
                        limit(1)
                    }.decodeSingleOrNull<ProfileDto>()
            } catch (e: Exception) { null }

            newProfile?.toUser(emailStr) ?: User(
                id = authUser.id,
                name = defaultName,
                email = emailStr,
                role = try { UserRole.valueOf(defaultRole) } catch (e: Exception) { UserRole.MEMBER },
                division = try {
                    UserDivision.valueOf(defaultDivision)
                } catch (e: Exception) { UserDivision.PUBDOK }
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateProfile(
        name: String, 
        phone: String?, 
        avatarUrl: String?,
        divisionHead: String?,
        staffList: String?,
        studentId: String?
    ): Result<User> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Tidak terautentikasi"))

            val updateData = buildJsonObject {
                put("name", name)
                if (phone != null) put("phone", phone)
                if (avatarUrl != null) put("avatar_url", avatarUrl)
                if (divisionHead != null) put("division_head", divisionHead)
                if (staffList != null) put("staff_list", staffList)
                if (studentId != null) put("student_id", studentId)
            }

            db["profiles"].update(updateData) { filter { eq("id", userId) } }
            val user = getCurrentUser()
                ?: return Result.failure(Exception("Gagal ambil data"))
            Result.success(user)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun updateAvatar(imageBytes: ByteArray, fileName: String): Result<String> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Tidak terautentikasi"))
            val bucket = client.storage["avatars"]
            val path = "$userId/$fileName"
            bucket.upload(path, imageBytes) { upsert = true }
            val url = bucket.publicUrl(path)

            val updateData = buildJsonObject {
                put("avatar_url", url)
            }
            db["profiles"].update(updateData) { filter { eq("id", userId) } }
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal upload foto: ${e.message}"))
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val profiles = db["profiles"].select(columns = Columns.ALL).decodeList<ProfileDto>()
            Result.success(profiles.map { it.toUser("") })
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val profile = db["profiles"].select(columns = Columns.ALL) {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<ProfileDto>()
            if (profile != null) {
                Result.success(profile.toUser(""))
            } else {
                Result.failure(Exception("User tidak ditemukan"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            adminDb["profiles"].delete { filter { eq("id", userId) } }
            try {
                httpClient.delete("${ApiConfig.supabaseUrl}/auth/v1/admin/users/$userId") {
                    header("apikey", ApiConfig.supabaseServiceRoleKey)
                    header("Authorization", "Bearer ${ApiConfig.supabaseServiceRoleKey}")
                }
            } catch (e: Exception) {
                // Delete auth user note
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun updateUserRole(userId: String, role: String): Result<Unit> {
        return try {
            val updateData = buildJsonObject {
                put("role", role)
            }
            db["profiles"].update(updateData) { filter { eq("id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun updateUserName(userId: String, name: String): Result<Unit> {
        return try {
            val updateData = buildJsonObject {
                put("name", name)
            }
            adminDb["profiles"].update(updateData) { filter { eq("id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun updateUserStudentId(userId: String, studentId: String?): Result<Unit> {
        return try {
            val updateData = buildJsonObject {
                put("student_id", studentId ?: "")
            }
            adminDb["profiles"].update(updateData) { filter { eq("id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun updateUserEmail(userId: String, email: String): Result<Unit> {
        return try {
            val bodyJson = """{ "email": "$email" }"""
            val response = httpClient.put("${ApiConfig.supabaseUrl}/auth/v1/admin/users/$userId") {
                header("apikey", ApiConfig.supabaseServiceRoleKey)
                header("Authorization", "Bearer ${ApiConfig.supabaseServiceRoleKey}")
                setBody(TextContent(bodyJson, ContentType.Application.Json))
            }
            if (response.status.isSuccess()) Result.success(Unit)
            else Result.failure(Exception("Gagal update email: ${response.status}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun updateUserPassword(userId: String, password: String): Result<Unit> {
        return try {
            val bodyJson = """{ "password": "$password" }"""
            val response = httpClient.put("${ApiConfig.supabaseUrl}/auth/v1/admin/users/$userId") {
                header("apikey", ApiConfig.supabaseServiceRoleKey)
                header("Authorization", "Bearer ${ApiConfig.supabaseServiceRoleKey}")
                setBody(TextContent(bodyJson, ContentType.Application.Json))
            }
            if (response.status.isSuccess()) Result.success(Unit)
            else Result.failure(Exception("Gagal update password: ${response.status}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    private fun ProfileDto.toUser(email: String) = User(
        id = id, name = name, email = email,
        role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.MEMBER },
        division = try {
            UserDivision.valueOf(division)
        } catch (e: Exception) { UserDivision.PUBDOK },
        studentId = studentId, phone = phone, avatarUrl = avatarUrl, isActive = isActive,
        divisionHead = divisionHead, staffList = staffList
    )

    private fun parseAuthError(message: String?): String = when {
        message == null -> "Terjadi kesalahan"
        message.contains("Invalid login credentials") -> "Email atau password salah"
        message.contains("Email not confirmed") -> "Email belum dikonfirmasi"
        message.contains("User already registered") ||
                message.contains("already been registered") ||
                message.contains("already registered") -> "Email sudah terdaftar"
        message.contains("Password should be") -> "Password minimal 6 karakter"
        message.contains("service_role") ||
                message.contains("401") -> "Service role key tidak valid"
        message.contains("Database error") ||
                message.contains("500") -> "Database error — jalankan SQL fix di Supabase SQL Editor"
        else -> message
    }
}
