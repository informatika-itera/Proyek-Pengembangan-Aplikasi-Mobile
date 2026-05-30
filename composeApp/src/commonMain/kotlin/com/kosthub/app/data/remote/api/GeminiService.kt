package com.kosthub.app.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

@Serializable
private data class GeminiRequest(val contents: List<GeminiContent>)

@Serializable
private data class GeminiContent(val parts: List<GeminiPart>)

@Serializable
private data class GeminiPart(val text: String)

@Serializable
private data class GeminiResponse(val candidates: List<GeminiCandidate> = emptyList())

@Serializable
private data class GeminiCandidate(val content: GeminiContent)

class GeminiService(private val client: HttpClient) {

    private val baseUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

    suspend fun generateRecommendation(prompt: String, apiKey: String): Result<String> {
        return try {
            val response = client.post("$baseUrl?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
                    )
                )
            }

            when {
                response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden -> {
                    Result.failure(Exception("API key tidak valid atau tidak memiliki akses. Periksa kembali GEMINI_API_KEY."))
                }
                response.status == HttpStatusCode.TooManyRequests -> {
                    Result.failure(Exception("Batas permintaan API terlampaui. Coba lagi beberapa saat."))
                }
                response.status == HttpStatusCode.ServiceUnavailable -> {
                    Result.failure(Exception("Layanan Gemini sedang tidak tersedia. Coba lagi nanti."))
                }
                !response.status.isSuccess() -> {
                    val body = runCatching { response.bodyAsText() }.getOrDefault("")
                    Result.failure(Exception("Gemini mengembalikan error ${response.status.value}: $body"))
                }
                else -> {
                    val geminiResponse = response.body<GeminiResponse>()
                    val text = geminiResponse.candidates
                        .firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: "Gemini tidak menghasilkan rekomendasi. Coba dengan deskripsi yang lebih detail."
                    Result.success(text)
                }
            }
        } catch (e: ConnectTimeoutException) {
            Result.failure(Exception("Koneksi ke Gemini timeout. Periksa koneksi internet kamu."))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Respons Gemini terlalu lama. Coba lagi."))
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("UnknownHostException") == true ||
                e.message?.contains("Unable to resolve") == true ->
                    "Tidak ada koneksi internet. Periksa jaringan kamu."
                e.message?.contains("SSLException") == true ->
                    "Koneksi tidak aman. Periksa pengaturan jaringan."
                else -> "Terjadi kesalahan tak terduga: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(message))
        }
    }
}

object GeminiPromptBuilder {

    fun buildRecommendationPrompt(kostData: String, userPreference: String): String = """
Kamu adalah asisten cerdas yang membantu mahasiswa menemukan kost yang paling sesuai.

Berikut adalah daftar kost yang tersedia:

$kostData

Preferensi mahasiswa: "$userPreference"

Tugasmu:
1. Pilih TOP 3 kost yang paling cocok dengan preferensi.
2. Jika tidak ada yang benar-benar cocok, rekomendasikan yang paling mendekati.

Format output yang HARUS diikuti dengan TEPAT (tanpa markdown, tanpa tanda bintang, tanpa simbol lain):

NAMA: [nama kost]
DETAIL: [harga per tahun] | [jarak] km dari kampus | Tipe: [tipe kost]
ALASAN: [penjelasan singkat maksimal 2 kalimat dalam bahasa Indonesia]

NAMA: [nama kost]
DETAIL: [harga per tahun] | [jarak] km dari kampus | Tipe: [tipe kost]
ALASAN: [penjelasan singkat maksimal 2 kalimat dalam bahasa Indonesia]

NAMA: [nama kost]
DETAIL: [harga per tahun] | [jarak] km dari kampus | Tipe: [tipe kost]
ALASAN: [penjelasan singkat maksimal 2 kalimat dalam bahasa Indonesia]

Jangan tambahkan teks apapun di luar format di atas.
    """.trimIndent()

    fun formatKostForPrompt(
        id: Long,
        namaKos: String,
        hargaTahunan: Long,
        jarakKm: Double,
        tipeKos: String,
        kamarMandi: String,
        wifi: String,
        fasilitasPendingin: String,
        furniturKasur: String,
        furniturLemari: String,
        furniturMejaBelajar: String,
        areaLaundry: String,
        areaDapur: String,
        keamananCctv: String
    ): String {
        val hargaFormatted = formatRupiah(hargaTahunan)
        val jarakFormatted = jarakKm.toString().replace(".", ",")
        val facilities = buildList {
            if (wifi == "Ada") add("WiFi")
            if (fasilitasPendingin != "Tidak ada") add(fasilitasPendingin)
            if (furniturKasur == "Ada") add("Kasur")
            if (furniturLemari == "Ada") add("Lemari")
            if (furniturMejaBelajar == "Ada") add("Meja Belajar")
            if (areaLaundry == "Ada") add("Laundry")
            if (areaDapur == "Ada") add("Dapur")
            if (keamananCctv == "Ada") add("CCTV")
        }.joinToString(", ").ifEmpty { "Tidak ada fasilitas tambahan" }

        return """
• Nama: $namaKos
  Harga: $hargaFormatted/tahun | Jarak dari kampus: $jarakFormatted km
  Tipe: $tipeKos | Kamar Mandi: $kamarMandi
  Fasilitas: $facilities
        """.trimIndent()
    }

    private fun formatRupiah(value: Long): String {
        val digits = value.toString()
        val grouped = digits.reversed().chunked(3).joinToString(".").reversed()
        return "Rp$grouped"
    }
}
