package com.movein.data.remote.api

import com.movein.data.remote.dto.GeminiResponseDto

interface GeminiApiService {
    suspend fun generateActivitySuggestion(mood: String): GeminiResponseDto
}