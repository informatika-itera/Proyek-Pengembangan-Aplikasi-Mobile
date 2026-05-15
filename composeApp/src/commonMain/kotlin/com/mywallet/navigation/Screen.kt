package com.mywallet.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object AddTransaction : Screen("add_transaction")
    object Profile : Screen("profile")

    object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: Int) = "transaction_detail/$transactionId"
    }

    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Int) = "edit_transaction/$transactionId"
    }
}

object NavArgs {
    const val TRANSACTION_ID = "transactionId"
}