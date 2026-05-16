package com.itera.news.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
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
            Berikan jawaban hanya satu kata kategorinya saja.
            
            Judul: $title
            Deskripsi: $description
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)
            val text = response.text?.trim() ?: "Netral"
            if (text.contains("Pro", ignoreCase = true)) "Pro"
            else if (text.contains("Kontra", ignoreCase = true)) "Kontra"
            else "Netral"
        } catch (e: Exception) {
            "Netral"
        }
    }
}
