package com.example.gamenews.presentation.navigation

interface NavigationActions {
    fun navigateToHome()
    fun navigateToGameDetail(gameId: Long)
    fun navigateToAIAssistant()
    fun navigateBack()
}