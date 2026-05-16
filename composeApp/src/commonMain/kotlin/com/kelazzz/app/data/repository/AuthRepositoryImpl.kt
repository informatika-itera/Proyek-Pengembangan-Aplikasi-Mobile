package com.kelazzz.app.data.repository

import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.data.remote.pocket.PocketApiService
import com.kelazzz.app.domain.model.User
import com.kelazzz.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Implementasi AuthRepository
 *
 * Menghubungkan PocketApiService (remote) dengan UserPreferences (local).
 * Setelah login berhasil, token + user info + device info disimpan di DataStore
 * untuk digunakan di request API selanjutnya.
 */
class AuthRepositoryImpl(
    private val apiService: PocketApiService,
    private val preferences: UserPreferences
) : AuthRepository {

    companion object {
        // Device info statis — bisa di-expand ke platform-specific jika perlu
        private const val DEVICE_NAME = "KelazZz"
        private const val DEVICE_ID = "KelazZz.Android.001"
    }

    override suspend fun login(username: String, password: String): Result<User> {
        return apiService.login(
            username = username,
            password = password,
            device = DEVICE_NAME,
            deviceId = DEVICE_ID
        ).mapCatching { response ->
            if (!response.meta.status || response.data == null) {
                throw Exception(response.meta.message)
            }

            val data = response.data
            val user = User(
                userId = data.userId,
                nama = data.nama,
                nim = data.nimnrk,
                email = data.email,
                unit = data.unit,
                level = data.level,
                photoUrl = data.photo,
                token = data.token
            )

            // Simpan semua ke DataStore untuk session persistence
            preferences.saveAuthToken(data.token)
            preferences.saveUserInfo(nim = data.nimnrk, name = data.nama)
            preferences.saveDeviceInfo(device = DEVICE_NAME, deviceId = DEVICE_ID)

            user
        }
    }

    override suspend fun logout() {
        preferences.clearAll()
    }

    override val isLoggedIn: Flow<Boolean> = preferences.isLoggedIn

    override val currentUser: Flow<User?> = combine(
        preferences.authToken,
        preferences.userNim,
        preferences.userName
    ) { token, nim, name ->
        if (token != null && nim != null && name != null) {
            User(
                userId = "",
                nama = name,
                nim = nim,
                email = "$nim@student.itera.ac.id",
                unit = "",
                level = "mahasiswa",
                photoUrl = "",
                token = token
            )
        } else null
    }
}
