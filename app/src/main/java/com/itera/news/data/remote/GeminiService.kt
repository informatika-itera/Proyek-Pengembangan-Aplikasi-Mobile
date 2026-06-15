package com.itera.news.data.remote

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(apiKey: String) {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun categorizeNews(title: String, description: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Kategorikan berita berikut tentang program "Makan Bergizi Gratis" (MBG) ke dalam salah satu dari 3 kategori saja: "Pro", "Kontra", atau "Netral".
            Berikan jawaban hanya satu kata kategorinya saja tanpa tanda baca atau penjelasan tambahan.
            
            Judul: $title
            Deskripsi: $description
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)
            val text = response.text?.trim() ?: "Netral"
            Log.d("GeminiService", "Kategori untuk '$title': $text")
            when {
                text.equals("Pro", ignoreCase = true) -> "Pro"
                text.equals("Kontra", ignoreCase = true) -> "Kontra"
                text.contains("Pro", ignoreCase = true) && !text.contains("Kontra", ignoreCase = true) -> "Pro"
                text.contains("Kontra", ignoreCase = true) -> "Kontra"
                else -> "Netral"
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Gagal kategorisasi untuk '$title': ${e.message}")
            "Netral"
        }
    }
}
