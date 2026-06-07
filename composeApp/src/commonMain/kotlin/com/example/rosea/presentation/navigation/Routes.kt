package com.example.rosea.presentation.navigation

object Routes {
    const val HOME = "home"
    const val AI_ASSISTANT = "ai_assistant"
    const val DETAIL = "detail/{productId}"
    const val CART = "cart"
    const val PROFILE = "profile"
    
    const val NOTIFICATIONS = "notifications"
    const val VOUCHERS = "vouchers"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val EDIT_PROFILE = "edit_profile"
    const val ADDRESS_MANAGEMENT = "address_management"
    const val HELP_SUPPORT = "help_support"

    // Fungsi bantuan untuk mengirim ID
    fun createDetailRoute(productId: Long): String {
        return "detail/$productId"
    }
}
