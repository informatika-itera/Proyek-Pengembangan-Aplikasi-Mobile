package com.example.foodsaver.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals

class FoodItemTest {

    private val now = Clock.System.now()

    @Test
    fun `getStatus should return EXPIRED when expiry date is in the past`() {
        val expiredFood = createFoodWithExpiry(now.plus(-2, DateTimeUnit.DAY, TimeZone.currentSystemDefault()))
        assertEquals(FoodStatus.EXPIRED, expiredFood.getStatus())
    }

    @Test
    fun `getStatus should return EXPIRED_TODAY when expiry date is today`() {
        val todayFood = createFoodWithExpiry(now)
        assertEquals(FoodStatus.EXPIRED_TODAY, todayFood.getStatus())
    }

    @Test
    fun `getStatus should return NEAR_EXPIRY when expiry date is between 1 to 3 days`() {
        val nearExpiryFood = createFoodWithExpiry(now.plus(2, DateTimeUnit.DAY, TimeZone.currentSystemDefault()))
        assertEquals(FoodStatus.NEAR_EXPIRY, nearExpiryFood.getStatus())
    }

    @Test
    fun `getStatus should return SAFE when expiry date is more than 3 days`() {
        val safeFood = createFoodWithExpiry(now.plus(5, DateTimeUnit.DAY, TimeZone.currentSystemDefault()))
        assertEquals(FoodStatus.SAFE, safeFood.getStatus())
    }

    @Test
    fun `getStatusLabel should return correct text for safe items`() {
        val food = createFoodWithExpiry(now.plus(5, DateTimeUnit.DAY, TimeZone.currentSystemDefault()))
        assertEquals("Masih segar, 5 hari lagi", food.getStatusLabel())
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
