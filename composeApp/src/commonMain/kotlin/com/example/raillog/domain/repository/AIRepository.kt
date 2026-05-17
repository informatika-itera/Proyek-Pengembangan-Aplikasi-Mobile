package com.example.raillog.domain.repository

interface AIRepository {
    suspend fun verifyDocument(content: String): Result<String>
    suspend fun summarizeInspection(report: String): Result<String>
    suspend fun suggestProcurement(supplyData: String): Result<String>
    suspend fun detectAnomalies(documentContent: String): Result<String>
}

enum class WritingStyle(val displayName: String, val prompt: String) {
    NEUTRAL("Netral", "Perbaiki tulisan dengan gaya netral"),
    FORMAL("Formal", "Perbaiki tulisan dengan gaya formal dan profesional"),
    CASUAL("Kasual", "Perbaiki tulisan dengan gaya santai dan friendly"),
    ACADEMIC("Akademik", "Perbaiki tulisan dengan gaya akademik dan ilmiah"),
    CREATIVE("Kreatif", "Perbaiki tulisan dengan gaya kreatif dan menarik")
}
