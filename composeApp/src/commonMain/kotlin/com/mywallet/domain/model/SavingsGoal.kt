package com.mywallet.domain.model

data class SavingsGoal(
    val id: Int = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val category: String,
    val color: String,
    val deadline: String? = null
)
