package com.mywallet.presentation.screens.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mywallet.domain.model.SavingsGoal
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType
import com.mywallet.domain.repository.SavingsGoalRepository
import com.mywallet.domain.repository.TransactionRepository
import com.mywallet.utils.getCurrentIsoDate
import com.mywallet.utils.getCurrentTime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavingsGoalViewModel(
    private val repository: SavingsGoalRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val goals: StateFlow<List<SavingsGoal>> = repository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addGoal(title: String, targetAmount: Double, category: String, color: String, deadline: String?) {
        viewModelScope.launch {
            repository.insertGoal(
                SavingsGoal(
                    title = title,
                    targetAmount = targetAmount,
                    currentAmount = 0.0,
                    category = category,
                    color = color,
                    deadline = deadline
                )
            )
        }
    }

    fun updateCurrentAmount(id: Int, amount: Double) {
        viewModelScope.launch {
            val goal = repository.getAllGoals().first().find { it.id == id } ?: return@launch
            val diff = amount - goal.currentAmount
            
            if (diff != 0.0) {
                // Update the goal amount
                repository.updateCurrentAmount(id, amount)
                
                // Add a transaction to reflect the balance change
                val dateStr = getCurrentIsoDate()
                val timeStr = getCurrentTime()
                
                transactionRepository.insertTransaction(
                    Transaction(
                        id = 0,
                        title = "Tabungan: ${goal.title}",
                        amount = if (diff > 0) diff else -diff,
                        type = if (diff > 0) TransactionType.EXPENSE else TransactionType.INCOME,
                        category = "Tabungan",
                        date = dateStr,
                        time = timeStr
                    )
                )
            }
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }
}
