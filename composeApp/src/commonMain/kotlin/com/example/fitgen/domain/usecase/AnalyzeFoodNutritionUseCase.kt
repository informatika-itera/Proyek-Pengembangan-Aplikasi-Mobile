package com.example.fitgen.domain.usecase

import com.example.fitgen.domain.repository.AIRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AnalyzeFoodNutritionUseCase(private val aiRepository: AIRepository) {

    suspend operator fun invoke(imageBytes: ByteArray): Result<NutritionData> {
        return try {
            // Prompt yang dioptimalkan untuk pengenalan makanan dan akurasi nutrisi
            val prompt = """
                Kamu adalah AI Ahli Gizi tingkat lanjut. Tugasmu adalah menganalisis foto makanan yang diberikan dengan sangat teliti.
                
                Lakukan langkah-langkah berikut:
                1. Identifikasi nama makanan secara spesifik (misalnya: Nasi Goreng Telur, Ayam Geprek, Seblak, atau Salad Buah). Jika ada beberapa jenis makanan dalam satu piring, gabungkan menjadi satu nama hidangan yang mendeskripsikannya.
                2. Berikan estimasi total nutrisi untuk 1 porsi standar hidangan yang terlihat di foto tersebut.
                3. Pastikan nilai Kalori (kkal), Protein (gram), Karbohidrat (gram), dan Lemak (gram) dikembalikan dalam bentuk angka bulat (integer).
            """.trimIndent()

            // Mengirim ke API
            val rawResponse = aiRepository.analyzeImage(prompt, imageBytes)

            // Membersihkan respons jika AI membandel memberi awalan markdown
            val cleanJsonString = rawResponse.replace("```json", "").replace("```", "").trim()

            // Parsing JSON ke dalam Data Class Kotlin
            val jsonObject = Json.parseToJsonElement(cleanJsonString).jsonObject
            val nutritionData = NutritionData(
                // Menangkap nama_makanan dari JSON balasan Gemini
                nama = jsonObject["nama_makanan"]?.jsonPrimitive?.content ?: "Makanan Tidak Dikenali",
                kalori = jsonObject["kalori"]?.jsonPrimitive?.int ?: 0,
                protein = jsonObject["protein"]?.jsonPrimitive?.int ?: 0,
                karbohidrat = jsonObject["karbohidrat"]?.jsonPrimitive?.int ?: 0,
                lemak = jsonObject["lemak"]?.jsonPrimitive?.int ?: 0
            )

            Result.success(nutritionData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Data class dengan parameter 'nama'
data class NutritionData(val nama: String, val kalori: Int, val protein: Int, val karbohidrat: Int, val lemak: Int)