package com.example.foodsaver.data.repository

import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.repository.FoodRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FoodRepositoryTest {
    
    private lateinit var repository: FoodRepository
    private val testItems = mutableListOf<FoodItem>()

    @BeforeTest
    fun setup() {
        testItems.clear()
        repository = object : FoodRepository {
            override fun getAllFoodItems() = kotlinx.coroutines.flow.flowOf(testItems)
            override suspend fun getFoodItemById(id: Long) = testItems.find { it.id == id }
            override suspend fun insertFoodItem(foodItem: FoodItem) {
                testItems.add(foodItem.copy(id = if(foodItem.id == 0L) testItems.size.toLong() + 1 else foodItem.id))
            }
            override suspend fun deleteFoodItem(id: Long) {
                testItems.removeIf { it.id == id }
            }
            override suspend fun updateFoodItem(foodItem: FoodItem) {
                val index = testItems.indexOfFirst { it.id == foodItem.id }
                if (index != -1) testItems[index] = foodItem
            }
        }
    }

    @Test
    fun `insertFoodItem should add item to repository`() = runTest {
        val newItem = createTestItem("Apel")
        repository.insertFoodItem(newItem)
        
        val items = repository.getAllFoodItems().first()
        assertEquals(1, items.size)
        assertEquals("Apel", items[0].name)
    }

    @Test
    fun `deleteFoodItem should remove item from repository`() = runTest {
        val item = createTestItem("Susu", id = 100)
        repository.insertFoodItem(item)
        repository.deleteFoodItem(100)
        
        val items = repository.getAllFoodItems().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun `updateFoodItem should modify existing item`() = runTest {
        val item = createTestItem("Telur", id = 1)
        repository.insertFoodItem(item)
        
        val updated = item.copy(quantity = 5.0)
        repository.updateFoodItem(updated)
        
        val result = repository.getFoodItemById(1)
        assertNotNull(result)
        assertEquals(5.0, result.quantity)
    }

    @Test
    fun `mark food as consumed should work via update`() = runTest {
        val item = createTestItem("Daging", id = 5)
        repository.insertFoodItem(item)
        
        repository.updateFoodItem(item.copy(isConsumed = true))
        val result = repository.getFoodItemById(5)
        assertTrue(result?.isConsumed == true)
    }

    private fun createTestItem(name: String, id: Long = 0) = FoodItem(
        id = id,
        name = name,
        quantity = 1.0,
        unit = "pcs",
        buyDate = Clock.System.now(),
        expiryDate = Clock.System.now(),
        category = "Lainnya"
    )
}
