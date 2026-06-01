package com.mywallet.data.repository

import com.mywallet.data.local.SavingsGoalLocalDataSource
import com.mywallet.data.model.toDomain
import com.mywallet.data.model.toEntity
import com.mywallet.domain.model.SavingsGoal
import com.mywallet.domain.repository.SavingsGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavingsGoalRepositoryImpl(
    private val localDataSource: SavingsGoalLocalDataSource
) : SavingsGoalRepository {

    override fun getAllGoals(): Flow<List<SavingsGoal>> =
        localDataSource.getAllGoals().map { list -> list.map { it.toDomain() } }

    override fun getGoalById(id: Int): Flow<SavingsGoal?> =
        localDataSource.getGoalById(id).map { it?.toDomain() }

    override suspend fun insertGoal(goal: SavingsGoal) =
        localDataSource.insertGoal(goal.toEntity())

    override suspend fun updateGoal(goal: SavingsGoal) =
        localDataSource.updateGoal(goal.toEntity())

    override suspend fun updateCurrentAmount(id: Int, amount: Double) =
        localDataSource.updateCurrentAmount(id, amount)

    override suspend fun deleteGoal(id: Int) =
        localDataSource.deleteGoal(id)
}
