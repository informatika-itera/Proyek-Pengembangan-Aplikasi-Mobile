package com.example.fitgen.domain.usecase

import com.example.fitgen.domain.repository.AIRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AnalyzeFoodNutritionUseCase(private val aiRepository: AIRepository) {

    suspend operator fun invoke(imageBytes: ByteArray): Result<NutritionData> {
        return try {
            val prompt = """
                Kamu adalah AI Ahli Gizi tingkat lanjut. Tugasmu adalah menganalisis foto makanan yang diberikan dengan sangat teliti.
                
                Lakukan langkah-langkah berikut:
                1. Identifikasi nama makanan secara spesifik (misalnya: Nasi Goreng Telur, Ayam Geprek, Seblak, atau Salad Buah). Jika ada beberapa jenis makanan dalam satu piring, gabungkan menjadi satu nama hidangan yang mendeskripsikannya.
                2. Berikan estimasi total nutrisi untuk 1 porsi standar hidangan yang terlihat di foto tersebut.
                3. Kembalikan HASILNYA HANYA dalam format JSON dengan struktur tepat seperti ini:
                   {
                     "nama_makanan": "Nama Hidangan",
                     "kalori": 350,
                     "protein": 15,
                     "karbohidrat": 45,
                     "lemak": 10
                   }
                4. Jangan berikan teks penjelasan lain di luar struktur JSON tersebut.
            """.trimIndent()

            val rawResponse = aiRepository.analyzeImage(prompt, imageBytes)

            val cleanJsonString = rawResponse.replace("```json", "").replace("```", "").trim()

            val jsonObject = Json.parseToJsonElement(cleanJsonString).jsonObject

            // Mekanisme ekstraksi defensif (Sprint 4 Edge Case Fix)
            val namaMakanan = jsonObject["nama_makanan"]?.jsonPrimitive?.content
                ?: jsonObject["nama"]?.jsonPrimitive?.content
                ?: "Makanan Tidak Dikenali"

            val kalori = jsonObject["kalori"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: jsonObject["kalori"]?.jsonPrimitive?.int
                ?: 0

            val protein = jsonObject["protein"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: jsonObject["protein"]?.jsonPrimitive?.int
                ?: 0

            val karbohidrat = jsonObject["karbohidrat"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: jsonObject["karbohidrat"]?.jsonPrimitive?.int
                ?: 0

            val lemak = jsonObject["lemak"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: jsonObject["lemak"]?.jsonPrimitive?.int
                ?: 0

            val nutritionData = NutritionData(
                nama = namaMakanan,
                kalori = kalori,
                protein = protein,
                karbohidrat = karbohidrat,
                lemak = lemak
            )

            Result.success(nutritionData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class NutritionData(val nama: String, val kalori: Int, val protein: Int, val karbohidrat: Int, val lemak: Int)