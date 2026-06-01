package com.mywallet.data.model

import com.mywallet.domain.model.SavingsGoal

data class SavingsGoalEntity(
    val id: Int,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val category: String,
    val color: String,
    val deadline: String?
)

fun SavingsGoalEntity.toDomain() = SavingsGoal(
    id = id,
    title = title,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    category = category,
    color = color,
    deadline = deadline
)

fun SavingsGoal.toEntity() = SavingsGoalEntity(
    id = id,
    title = title,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    category = category,
    color = color,
    deadline = deadline
)
