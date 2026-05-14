package com.example.pocketguard.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data class AddTransaction(val transactionId: Long? = null) : Route

    @Serializable
    data class TransactionDetail(val transactionId: Long) : Route

    @Serializable
    data class AIAssistant(val initialText: String? = null) : Route
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToAddTransaction(transactionId: Long? = null)
    fun navigateToTransactionDetail(transactionId: Long)
    fun navigateToAIAssistant(initialText: String? = null)
    fun navigateBack()
}