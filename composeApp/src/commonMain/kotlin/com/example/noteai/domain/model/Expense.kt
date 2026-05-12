package com.example.noteai.domain.model

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Long
)