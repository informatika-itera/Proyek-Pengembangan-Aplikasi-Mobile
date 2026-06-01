package com.mywallet.fakes

import com.mywallet.domain.model.SavingsGoal
import com.mywallet.domain.repository.SavingsGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSavingsGoalRepository : SavingsGoalRepository {
    private val goals = MutableStateFlow<List<SavingsGoal>>(emptyList())

    override fun getAllGoals(): Flow<List<SavingsGoal>> = goals

    override fun getGoalById(id: Int): Flow<SavingsGoal?> = 
        MutableStateFlow(goals.value.find { it.id == id })

    override suspend fun insertGoal(goal: SavingsGoal) {
        val currentList = goals.value.toMutableList()
        val newGoal = if (goal.id == 0) {
            goal.copy(id = (currentList.maxOfOrNull { it.id } ?: 0) + 1)
        } else {
            goal
        }
        currentList.add(newGoal)
        goals.value = currentList
    }

    override suspend fun updateGoal(goal: SavingsGoal) {
        val currentList = goals.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == goal.id }
        if (index != -1) {
            currentList[index] = goal
            goals.value = currentList
        }
    }

    override suspend fun updateCurrentAmount(id: Int, amount: Double) {
        val currentList = goals.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(currentAmount = amount)
            goals.value = currentList
        }
    }

    override suspend fun deleteGoal(id: Int) {
        val currentList = goals.value.toMutableList()
        currentList.removeAll { it.id == id }
        goals.value = currentList
    }
}
