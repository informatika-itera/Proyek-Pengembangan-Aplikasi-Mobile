package com.example.noteai.domain.usecase

import com.example.noteai.domain.model.Expense

class GetExpenses(
    private val expenses: MutableList<Expense> = mutableListOf()
) {
    operator fun invoke(): List<Expense> {
        return expenses
    }
}