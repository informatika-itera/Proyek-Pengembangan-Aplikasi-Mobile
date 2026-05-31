package com.example.inventra.data.repository

import app.cash.turbine.test
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.model.ItemCondition
import com.example.inventra.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ItemRepositoryTest {
    
    private lateinit var repository: FakeItemRepository
    
    @BeforeTest
    fun setup() {
        repository = FakeItemRepository()
    }
    
    @Test
    fun `insertItem should return new item id`() = runTest {
        val item = createTestItem(name = "Test Item")
        val id = repository.insertItem(item)
        assertTrue(id > 0)
    }
    
    @Test
    fun `insertItem should add item to list`() = runTest {
        val item = createTestItem(name = "New Item")
        repository.insertItem(item)
        
        repository.getAllItems().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("New Item", items.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getAllItems should return all items`() = runTest {
        repository.insertItem(createTestItem(name = "Item 1"))
        repository.insertItem(createTestItem(name = "Item 2"))
        
        repository.getAllItems().test {
            val items = awaitItem()
            assertEquals(2, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getItemById should return correct item`() = runTest {
        val id = repository.insertItem(createTestItem(name = "Find Me"))
        
        repository.getItemById(id).test {
            val item = awaitItem()
            assertNotNull(item)
            assertEquals("Find Me", item.name)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `searchItems should find items by name`() = runTest {
        repository.insertItem(createTestItem(name = "Projector"))
        repository.insertItem(createTestItem(name = "Marker"))
        
        repository.searchItems("Project").test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Projector", items.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `deleteItem should remove item from list`() = runTest {
        val id = repository.insertItem(createTestItem(name = "To Delete"))
        repository.deleteItem(id)
        
        repository.getAllItems().test {
            val items = awaitItem()
            assertTrue(items.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    private fun createTestItem(
        id: Long = 0,
        name: String = "Test",
        category: ItemCategory = ItemCategory.OTHER
    ): Item {
        return Item(
            id = id,
            name = name,
            category = category,
            location = "Lab",
            totalStock = 10,
            availableStock = 10,
            condition = ItemCondition.GOOD,
            picName = "Naufal",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

class FakeItemRepository : ItemRepository {
    private val items = MutableStateFlow<List<Item>>(emptyList())
    private var nextId = 1L
    
    override fun getAllItems(): Flow<List<Item>> = items
    
    override fun getItemsByCategory(category: ItemCategory): Flow<List<Item>> {
        return items.map { list -> list.filter { it.category == category } }
    }
    
    override fun searchItems(query: String): Flow<List<Item>> {
        return items.map { list ->
            list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
    }
    
    override fun getItemById(id: Long): Flow<Item?> {
        return items.map { list -> list.find { it.id == id } }
    }
    
    override suspend fun insertItem(item: Item): Long {
        val id = nextId++
        val newItem = item.copy(id = id)
        items.update { it + newItem }
        return id
    }
    
    override suspend fun updateItem(item: Item) {
        items.update { list ->
            list.map { if (it.id == item.id) item else it }
        }
    }
    
    override suspend fun deleteItem(id: Long) {
        items.update { list -> list.filter { it.id != id } }
    }
}
