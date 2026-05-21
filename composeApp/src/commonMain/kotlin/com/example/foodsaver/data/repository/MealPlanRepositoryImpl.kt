package com.example.foodsaver.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.foodsaver.data.local.FoodSaverDatabase
import com.example.foodsaver.domain.model.MealPlan
import com.example.foodsaver.domain.model.MealType
import com.example.foodsaver.domain.repository.MealPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class MealPlanRepositoryImpl(
    private val db: FoodSaverDatabase
) : MealPlanRepository {

    private val queries = db.mealPlanQueries

    override fun getMealPlansForDate(date: LocalDate): Flow<List<MealPlan>> {
        val startOfDay = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        return queries.getMealPlansByDate(startOfDay)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun addMealPlan(mealPlan: MealPlan) {
        val epochMillis = mealPlan.date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        queries.insertMealPlan(
            recipeId = mealPlan.recipeId,
            recipeName = mealPlan.recipeName,
            recipeImageUrl = mealPlan.recipeImageUrl,
            date = epochMillis,
            mealType = mealPlan.mealType.name
        )
    }

    override suspend fun removeMealPlan(id: Long) {
        queries.deleteMealPlan(id)
    }

    override fun getAllMealPlans(): Flow<List<MealPlan>> {
        return queries.getAllMealPlans()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    private fun com.example.foodsaver.data.local.MealPlanEntity.toDomain(): MealPlan {
        return MealPlan(
            id = id,
            recipeId = recipeId,
            recipeName = recipeName,
            recipeImageUrl = recipeImageUrl,
            date = kotlinx.datetime.Instant.fromEpochMilliseconds(date)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date,
            mealType = MealType.valueOf(mealType)
        )
    }
}
