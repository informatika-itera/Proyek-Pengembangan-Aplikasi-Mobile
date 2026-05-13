package com.example.gamenews.domain.usecase

import com.example.gamenews.domain.model.Expense

class AddExpense(
    private val expenses: MutableList<Expense> = mutableListOf()
) {
    operator fun invoke(expense: Expense) {
        expenses.add(expense)
    }
}