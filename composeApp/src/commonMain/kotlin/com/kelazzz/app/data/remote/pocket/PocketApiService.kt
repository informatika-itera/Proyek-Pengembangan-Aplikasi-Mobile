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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

// ==================== DTOs ====================

@Serializable
data class PocketMeta(
    val message: String,
    val status: Boolean,
    val code: Int
)

@Serializable
data class KelasResponse(
    val meta: PocketMeta,
    val data: List<KelasData> = emptyList()
)

@Serializable
data class TokenResponse(
    val meta: PocketMeta
)

@Serializable
data class KelasData(
    @SerialName("nomor_mk") val nomorMk: String,
    @SerialName("kode_mk") val kodeMk: String,
    @SerialName("kode_kelas") val kodeKelas: String,
    @SerialName("nama_kelas") val namaKelas: String,
    val mode: String? = null,
    @SerialName("nama_mk") val namaMk: String,
    @SerialName("sks_mk") val sksMk: String,
    @SerialName("nama_dosen_list") val namaDosenList: String,
    @SerialName("jadwal_hari") val jadwalHari: String
)

@Serializable
data class PresensiResponse(
    val meta: PocketMeta,
    val data: List<PresensiData> = emptyList()
)

@Serializable
data class PresensiData(
    @SerialName("no_pertemuan") val noPertemuan: Int,
    @SerialName("pertemuan") val pertemuan: String? = null,
    @SerialName("waktu_mulai") val waktuMulai: String? = null,
    @SerialName("mhs_masuk") val mhsMasuk: String? = null,
    @SerialName("mhs_tdkmasuk") val mhsTdkMasuk: String? = null,
    @SerialName("mhs_jumlah") val mhsJumlah: String? = null,
    @SerialName("absen_mahasiswa") val absenMahasiswa: String? = null
)

@Serializable
data class LoginResponse(
    val meta: PocketMeta,
    @Serializable(with = LoginDataSerializer::class)
    val data: LoginData? = null
)

/**
 * Custom serializer untuk field `data` di LoginResponse.
 *
 * API Pocket ITERA mengembalikan format berbeda tergantung status:
 * - Login berhasil: "data": { ... }  (JSON object)
 * - Login gagal:    "data": []       (JSON array kosong)
 *
 * Serializer ini mendeteksi jika `data` adalah JsonArray dan
 * mengkonversinya ke null agar tidak crash saat deserialization.
 */
object LoginDataSerializer : KSerializer<LoginData?> {
    private val delegateSerializer = LoginData.serializer()
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: LoginData?) {
        if (value != null) {
            encoder.encodeSerializableValue(delegateSerializer, value)
        } else {
            encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: Decoder): LoginData? {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return decoder.decodeSerializableValue(delegateSerializer)
        
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonObject -> jsonDecoder.json.decodeFromJsonElement(delegateSerializer, element)
            is JsonNull -> null
            is JsonArray -> null  // API returns [] on failed login
            else -> null
        }
    }
}

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

    /**
     * Mengambil daftar kelas/mata kuliah aktif mahasiswa
     *
     * @param token Token auth dari Pocket
     * @param deviceId Device ID untuk X-Device-Id header
     * @param nim NIM mahasiswa
     */
    suspend fun getKelas(
        token: String,
        deviceId: String,
        nim: String
    ): Result<KelasResponse> {
        return try {
            val response = client.submitForm(
                url = "$BASE_URL/mahasiswa/kelas",
                formParameters = Parameters.build {
                    append("nim", nim)
                }
            ) {
                header("User-Agent", USER_AGENT)
                header("Accept", "*/*")
                header("Authorization", token)
                header("X-Device-Id", deviceId)
            }
            Result.success(response.body<KelasResponse>())
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(Exception("Koneksi timeout saat mengambil daftar kelas."))
        } catch (e: ClientRequestException) {
            val errorMessage = try {
                val errorBody = e.response.body<KelasResponse>()
                errorBody.meta.message
            } catch (_: Exception) {
                "Gagal mengambil kelas. Token tidak valid atau sesi kedaluwarsa."
            }
            Result.failure(Exception(errorMessage))
        } catch (e: ServerResponseException) {
            Result.failure(Exception("Server ITERA sedang gangguan (${e.response.status.value})."))
        } catch (e: SerializationException) {
            Result.failure(Exception("Server mengembalikan format kelas yang tidak valid."))
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet untuk memperbarui daftar kelas."))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan: ${e.message ?: "Kesalahan tidak diketahui"}"))
        }
    }

    /**
     * Melakukan registrasi token untuk mengaktifkan sesi (sebelum memanggil getKelas/presensi)
     *
     * @param token Token auth dari Pocket
     * @param deviceId Device ID untuk X-Device-Id header dan parameter device
     * @param email Email mahasiswa
     */
    suspend fun registerToken(
        token: String,
        deviceId: String,
        email: String
    ): Result<TokenResponse> {
        return try {
            val response = client.submitForm(
                url = "$BASE_URL/auth/token",
                formParameters = Parameters.build {
                    append("device", deviceId)
                    append("email", email)
                }
            ) {
                header("User-Agent", USER_AGENT)
                header("Accept", "*/*")
                header("Authorization", token)
                header("X-Device-Id", deviceId)
            }
            Result.success(response.body<TokenResponse>())
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(Exception("Koneksi timeout saat registrasi token."))
        } catch (e: ClientRequestException) {
            val errorMessage = try {
                val errorBody = e.response.body<TokenResponse>()
                errorBody.meta.message
            } catch (_: Exception) {
                "Gagal otorisasi token. Sesi tidak valid."
            }
            Result.failure(Exception(errorMessage))
        } catch (e: ServerResponseException) {
            Result.failure(Exception("Server ITERA sedang gangguan saat otorisasi (${e.response.status.value})."))
        } catch (e: SerializationException) {
            Result.failure(Exception("Server mengembalikan format otorisasi yang tidak valid."))
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet untuk otorisasi."))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan otorisasi: ${e.message ?: "Kesalahan tidak diketahui"}"))
        }
    }

    /**
     * Mengambil riwayat detail kehadiran pertemuan per mata kuliah
     *
     * @param token Token auth dari Pocket
     * @param deviceId Device ID
     * @param nim NIM mahasiswa
     * @param kelasKode Kode kelas (kode_kelas dari data kelas)
     */
    suspend fun getPresensiDetail(
        token: String,
        deviceId: String,
        nim: String,
        kelasKode: String
    ): Result<PresensiResponse> {
        return try {
            val response = client.submitForm(
                url = "$BASE_URL/presensi/data_mahasiswa",
                formParameters = Parameters.build {
                    append("nim", nim)
                    append("kelas", kelasKode)
                }
            ) {
                header("User-Agent", USER_AGENT)
                header("Accept", "*/*")
                header("Authorization", token)
                header("X-Device-Id", deviceId)
            }
            Result.success(response.body<PresensiResponse>())
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(Exception("Koneksi timeout saat mengambil detail presensi."))
        } catch (e: ClientRequestException) {
            val errorMessage = try {
                val errorBody = e.response.body<PresensiResponse>()
                errorBody.meta.message
            } catch (_: Exception) {
                "Gagal mengambil detail presensi. Sesi tidak valid."
            }
            Result.failure(Exception(errorMessage))
        } catch (e: ServerResponseException) {
            Result.failure(Exception("Server ITERA sedang gangguan saat mengambil presensi (${e.response.status.value})."))
        } catch (e: SerializationException) {
            Result.failure(Exception("Server mengembalikan format presensi yang tidak valid."))
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet untuk memperbarui detail presensi."))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan: ${e.message ?: "Kesalahan tidak diketahui"}"))
        }
    }
}

/**
 * Exception khusus untuk error login dengan pesan user-friendly
 */
class LoginException(message: String) : Exception(message)
