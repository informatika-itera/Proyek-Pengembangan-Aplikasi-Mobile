package com.example.raillog.data.repository

import com.example.raillog.data.remote.api.GeminiService
import com.example.raillog.data.remote.api.SystemPrompts
import com.example.raillog.domain.repository.AIRepository

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun verifyDocument(content: String): Result<String> {
        return geminiService.generateContent(
            prompt = "Verifikasi dokumen ini:\n\n$content",
            systemPrompt = SystemPrompts.DOCUMENT_VERIFIER
        )
    }

    override suspend fun summarizeInspection(report: String): Result<String> {
        return geminiService.generateContent(
            prompt = "Ringkas laporan inspeksi ini:\n\n$report",
            systemPrompt = SystemPrompts.INSPECTION_SUMMARIZER
        )
    }

    override suspend fun suggestProcurement(supplyData: String): Result<String> {
        return geminiService.generateContent(
            prompt = "Berikan saran pengadaan untuk data berikut:\n\n$supplyData",
            systemPrompt = SystemPrompts.SUPPLY_ADVISOR
        )
    }

    override suspend fun detectAnomalies(documentContent: String): Result<String> {
        return geminiService.generateContent(
            prompt = "Deteksi anomali pada dokumen teknis berikut:\n\n$documentContent",
            systemPrompt = SystemPrompts.ANOMALY_DETECTOR
        )
    }
}