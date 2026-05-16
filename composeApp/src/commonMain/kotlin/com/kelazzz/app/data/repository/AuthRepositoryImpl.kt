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
        val apiResult = apiService.login(
            username = username,
            password = password,
            device = DEVICE_NAME,
            deviceId = DEVICE_ID
        )

        return apiResult.mapCatching { response ->
            // API mengembalikan status false berarti kredensial salah atau error lain
            if (!response.meta.status) {
                val message = response.meta.message.ifBlank {
                    "Login gagal. Periksa email dan password Anda."
                }
                throw Exception(message)
            }

            val data = response.data
                ?: throw Exception("Login gagal. Data pengguna tidak ditemukan dari server.")

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

            // Validasi token tidak kosong sebelum menyimpan
            if (data.token.isBlank()) {
                throw Exception("Login gagal. Token autentikasi tidak valid.")
            }

            // Simpan semua ke DataStore untuk session persistence
            try {
                preferences.saveAuthToken(data.token)
                preferences.saveUserInfo(
                    nim = data.nimnrk,
                    name = data.nama,
                    email = data.email,
                    photoUrl = data.photo.replace("http://", "https://")
                )
                preferences.saveDeviceInfo(device = DEVICE_NAME, deviceId = DEVICE_ID)
            } catch (e: Exception) {
                throw Exception("Login berhasil tetapi gagal menyimpan sesi. Coba login ulang.")
            }

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
        preferences.userName,
        preferences.userEmail,
        preferences.userPhotoUrl
    ) { values ->
        val token = values[0]
        val nim = values[1]
        val name = values[2]
        val email = values[3]
        val photoUrl = values[4]
        if (token != null && nim != null && name != null) {
            User(
                userId = "",
                nama = name,
                nim = nim,
                email = email ?: "$nim@student.itera.ac.id",
                unit = "",
                level = "mahasiswa",
                photoUrl = photoUrl ?: "",
                token = token
            )
        } else null
    }
}
