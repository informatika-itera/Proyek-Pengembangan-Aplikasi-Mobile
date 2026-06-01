package com.mywallet.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.db.SqlDriver
import com.mywallet.data.model.SavingsGoalEntity
import com.mywallet.db.WalletDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavingsGoalLocalDataSource(driver: SqlDriver) {
    private val database = WalletDatabase(driver)
    private val queries = database.savingsGoalQueries

    fun getAllGoals(): Flow<List<SavingsGoalEntity>> =
        queries.selectAllGoals().asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toDataEntity() }
        }

    fun getGoalById(id: Int): Flow<SavingsGoalEntity?> =
        queries.selectGoalById(id.toLong()).asFlow().mapToOneOrNull(Dispatchers.Default).map { it?.toDataEntity() }

    fun insertGoal(entity: SavingsGoalEntity) {
        queries.insertGoal(entity.title, entity.targetAmount, entity.currentAmount, entity.category, entity.color, entity.deadline)
    }

    fun updateGoal(entity: SavingsGoalEntity) {
        queries.updateGoal(entity.title, entity.targetAmount, entity.currentAmount, entity.category, entity.color, entity.deadline, entity.id.toLong())
    }

    fun updateCurrentAmount(id: Int, amount: Double) {
        queries.updateCurrentAmount(amount, id.toLong())
    }

    fun deleteGoal(id: Int) {
        queries.deleteGoal(id.toLong())
    }

    private fun com.mywallet.db.SavingsGoalDbEntity.toDataEntity() = SavingsGoalEntity(
        id = id.toInt(),
        title = title,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        category = category,
        color = color,
        deadline = deadline
    )
}
