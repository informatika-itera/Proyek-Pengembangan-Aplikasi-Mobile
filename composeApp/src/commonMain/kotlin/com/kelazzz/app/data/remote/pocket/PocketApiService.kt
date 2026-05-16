package com.kelazzz.app.data.remote.pocket

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable

// ==================== DTOs ====================

@Serializable
data class PocketMeta(
    val message: String,
    val status: Boolean,
    val code: Int
)

@Serializable
data class LoginResponse(
    val meta: PocketMeta,
    val data: LoginData? = null
)

@Serializable
data class LoginData(
    val userId: String,
    val nama: String,
    val level: String,
    val nomorId: String,
    val unit: String,
    val photo: String,
    val email: String,
    val nimnrk: String,
    val token: String
)

// ==================== SERVICE ====================

/**
 * Pocket ITERA API Service
 *
 * Base URL: https://api.itera.ac.id/v2
 *
 * PENTING:
 * - User-Agent HARUS "Dart/3.8 (dart:io)" agar lolos Cloudflare
 * - Device dan device_id disimpan untuk header X-device-id di request selanjutnya
 */
class PocketApiService(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "https://api.itera.ac.id/v2"
        private const val USER_AGENT = "Dart/3.8 (dart:io)"
    }

    /**
     * Login ke Pocket ITERA
     *
     * @param username Email ITERA (format: nama.nim@student.itera.ac.id)
     * @param password Password akun ITERA
     * @param device Nama device (e.g. "NE2211")
     * @param deviceId Device ID (e.g. "SKQ1.220617.001")
     */
    suspend fun login(
        username: String,
        password: String,
        device: String,
        deviceId: String
    ): Result<LoginResponse> {
        return try {
            val response = client.submitForm(
                url = "$BASE_URL/auth/login",
                formParameters = Parameters.build {
                    append("username", username)
                    append("password", password)
                    append("device", device)
                    append("device_id", deviceId)
                }
            ) {
                header("User-Agent", USER_AGENT)
                header("Accept", "application/json")
            }
            Result.success(response.body<LoginResponse>())
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(
                LoginException("Koneksi timeout. Server ITERA sedang lambat, coba lagi nanti.")
            )
        } catch (e: ClientRequestException) {
            // HTTP 4xx — coba parse pesan error dari body response
            val errorMessage = try {
                val errorBody = e.response.body<LoginResponse>()
                errorBody.meta.message
            } catch (_: Exception) {
                "Email atau password salah. Periksa kembali kredensial Anda."
            }
            Result.failure(LoginException(errorMessage))
        } catch (e: ServerResponseException) {
            // HTTP 5xx — server error
            Result.failure(
                LoginException("Server ITERA sedang gangguan (${e.response.status.value}). Coba lagi nanti.")
            )
        } catch (e: SerializationException) {
            // Response tidak sesuai format (mungkin HTML dari Cloudflare)
            Result.failure(
                LoginException("Server mengembalikan format yang tidak valid. Coba lagi nanti.")
            )
        } catch (e: IOException) {
            // Tidak ada koneksi internet / DNS gagal
            Result.failure(
                LoginException("Tidak ada koneksi internet. Periksa jaringan Anda dan coba lagi.")
            )
        } catch (e: Exception) {
            // Fallback untuk error yang tidak terduga
            Result.failure(
                LoginException("Terjadi kesalahan: ${e.message ?: "Kesalahan tidak diketahui"}")
            )
        }
    }
}

/**
 * Exception khusus untuk error login dengan pesan user-friendly
 */
class LoginException(message: String) : Exception(message)
