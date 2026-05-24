package com.example.rosea.presentation.navigation

object Routes {
    const val HOME = "home"
    const val AI_ASSISTANT = "ai_assistant"
    const val DETAIL = "detail/{productId}"
    const val CART = "cart"
    const val PROFILE = "profile" // 👈

    // Fungsi bantuan untuk mengirim ID
    fun createDetailRoute(productId: Long): String {
        return "detail/$productId"
    }
}