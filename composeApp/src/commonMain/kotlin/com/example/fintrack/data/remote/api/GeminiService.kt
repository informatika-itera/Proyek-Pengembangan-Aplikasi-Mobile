package com.example.fintrack.data.remote.api

import com.example.fintrack.core.network.ApiConfig
import com.example.fintrack.data.remote.dto.GeminiContent
import com.example.fintrack.data.remote.dto.GeminiPart
import com.example.fintrack.data.remote.dto.GeminiRequest
import com.example.fintrack.data.remote.dto.GeminiResponse
import com.example.fintrack.data.remote.dto.getErrorMessage
import com.example.fintrack.data.remote.dto.getTextContent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class GeminiService(private val client: HttpClient) {
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        val apiKey = ApiConfig.geminiApiKey
        if (apiKey.isBlank()) {
            throw Exception("API Key belum disetting di local.properties")
        }

        val url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey"
        
        // Buat konten berdasarkan apakah ada system prompt atau tidak
        val parts = mutableListOf<GeminiPart>()
        if (systemPrompt != null) {
            parts.add(GeminiPart(text = "System: $systemPrompt"))
        }
        parts.add(GeminiPart(text = prompt))
        
        val requestBody = GeminiRequest(
            contents = listOf(GeminiContent(parts = parts))
        )

        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        val responseData: GeminiResponse = response.body()

        if (response.status.isSuccess()) {
            val text = responseData.getTextContent()
            if (text != null) {
                text
            } else {
                throw Exception("Response kosong dari Gemini")
            }
        } else {
            val errorMsg = responseData.getErrorMessage() ?: "Terjadi kesalahan pada server"
            throw Exception(errorMsg)
        }
    }
}

object SystemPrompts {
    val SUMMARIZER = "summarizer prompt"
    val IDEA_GENERATOR = "idea generator prompt"
    val WRITING_IMPROVER = "writing improver prompt"
    val TITLE_SUGGESTER = "title suggester prompt"
    val TRANSLATOR = "translator prompt"
    val FINANCIAL_ADVISOR = "Kamu adalah penasihat keuangan pribadi. Berikan komentar super singkat (maksimal 2 kalimat). Jika ada nama transaksi atau pengeluaran yang aneh, lucu, atau tidak wajar, berikan sentilan atau komentar spesifik terkait transaksi tersebut. Jangan pakai markdown."
}
