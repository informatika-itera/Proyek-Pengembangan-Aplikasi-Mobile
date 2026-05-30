package com.example.foodsaver.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FoodItemTest {

    private val now = Clock.System.now()
    private val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

    @Test
    fun `getStatus should return EXPIRED when expiry date is in the past`() {
        val expiredFood = createFoodWithExpiry(now.plus(-2, DateTimeUnit.DAY, TimeZone.currentSystemDefault()))
        assertEquals(FoodStatus.EXPIRED, expiredFood.getStatus())
    }

    @Test
    fun `getStatus should return NEAR_EXPIRY when expiry date is within 3 days`() {
        val nearExpiryFood = createFoodWithExpiry(now.plus(2, DateTimeUnit.DAY, TimeZone.currentSystemDefault()))
        assertEquals(FoodStatus.NEAR_EXPIRY, nearExpiryFood.getStatus())
    }

    @Test
    fun `getStatus should return SAFE when expiry date is far in the future`() {
        val safeFood = createFoodWithExpiry(now.plus(10, DateTimeUnit.DAY, TimeZone.currentSystemDefault()))
        assertEquals(FoodStatus.SAFE, safeFood.getStatus())
    }

    @Test
    fun `getDaysRemaining should return correct count`() {
        val food = createFoodWithExpiry(now.plus(5, DateTimeUnit.DAY, TimeZone.currentSystemDefault()))
        assertEquals(5, food.getDaysRemaining())
    }

    private fun createFoodWithExpiry(expiry: kotlinx.datetime.Instant): FoodItem {
        return FoodItem(
            id = 1,
            name = "Test Food",
            quantity = 1.0,
            unit = "pcs",
            buyDate = now,
            expiryDate = expiry,
            category = "Lainnya"
        )
    }
}
