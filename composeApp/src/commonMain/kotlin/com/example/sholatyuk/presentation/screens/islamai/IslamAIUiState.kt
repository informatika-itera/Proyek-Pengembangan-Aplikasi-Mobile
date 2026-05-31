package com.example.sholatyuk.presentation.screens.islamai

import com.example.sholatyuk.domain.model.ChatMessage

data class IslamAIUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)