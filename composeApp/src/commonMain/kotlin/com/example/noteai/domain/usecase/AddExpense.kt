package com.example.noteai.domain.usecase

import com.example.noteai.domain.model.Expense

class AddExpense(
    private val expenses: MutableList<Expense> = mutableListOf()
) {
    operator fun invoke(expense: Expense) {
        expenses.add(expense)
    }
}