package com.mywallet.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object History : Screen("history")
    object Statistics : Screen("statistics")
    object AddTransaction : Screen("add_transaction")
    object Profile : Screen("profile")
    object SavingsGoal : Screen("savings_goal")

    object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: Int) = "transaction_detail/$transactionId"
    }

    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Int) = "edit_transaction/$transactionId"
    }

    object SettingsDetail : Screen("settings_detail/{title}") {
        fun createRoute(title: String) = "settings_detail/$title"
    }
}
