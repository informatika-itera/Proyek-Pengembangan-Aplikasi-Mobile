package com.example.gamenews.domain.usecase

import com.example.gamenews.domain.model.Expense

class GetExpenses(
    private val expenses: MutableList<Expense> = mutableListOf()
) {
    operator fun invoke(): List<Expense> {
        return expenses
    }
}