package com.mywallet.domain.repository

import com.mywallet.domain.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

interface SavingsGoalRepository {
    fun getAllGoals(): Flow<List<SavingsGoal>>
    fun getGoalById(id: Int): Flow<SavingsGoal?>
    suspend fun insertGoal(goal: SavingsGoal)
    suspend fun updateGoal(goal: SavingsGoal)
    suspend fun updateCurrentAmount(id: Int, amount: Double)
    suspend fun deleteGoal(id: Int)
}
