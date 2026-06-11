package com.example.inventra.domain.usecase

import app.cash.turbine.test
import com.example.inventra.FakeItemRepository
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.model.ItemCondition
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ItemUseCaseTest {

    private lateinit var repository: FakeItemRepository
    private lateinit var getAllItemsUseCase: GetAllItemsUseCase
    private lateinit var saveItemUseCase: SaveItemUseCase
    private lateinit var deleteItemUseCase: DeleteItemUseCase
    private lateinit var searchItemsUseCase: SearchItemsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeItemRepository()
        getAllItemsUseCase = GetAllItemsUseCase(repository)
        saveItemUseCase = SaveItemUseCase(repository)
        deleteItemUseCase = DeleteItemUseCase(repository)
        searchItemsUseCase = SearchItemsUseCase(repository)
    }

    // Test 1: Sort ascending
    @Test
    fun `getAllItems with NAME_ASC should return items sorted alphabetically`() = runTest {
        repository.insertItem(createItem("Zebra Mic"))
        repository.insertItem(createItem("Apple Stand"))
        repository.insertItem(createItem("Mango Speaker"))

        getAllItemsUseCase(ItemSortBy.NAME_ASC).test {
            val items = awaitItem()
            assertEquals(3, items.size)
            assertEquals("Apple Stand", items[0].name)
            assertEquals("Mango Speaker", items[1].name)
            assertEquals("Zebra Mic", items[2].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 2: Sort descending
    @Test
    fun `getAllItems with NAME_DESC should return items in reverse alphabetical order`() = runTest {
        repository.insertItem(createItem("Apple"))
        repository.insertItem(createItem("Mango"))

        getAllItemsUseCase(ItemSortBy.NAME_DESC).test {
            val items = awaitItem()
            assertEquals("Mango", items[0].name)
            assertEquals("Apple", items[1].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 3: Save item dengan nama kosong harus gagal
    @Test
    fun `saveItem with blank name should return failure with error message`() = runTest {
        val item = createItem(name = "")
        val result = saveItemUseCase(item)

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error?.message?.isNotBlank() == true)
    }

    // Test 4: Save item dengan stok 0 harus gagal
    @Test
    fun `saveItem with zero stock should return failure`() = runTest {
        val item = createItem(name = "Valid Item", stock = 0)
        val result = saveItemUseCase(item)

        assertTrue(result.isFailure)
    }

    // Test 5: Save item valid harus sukses
    @Test
    fun `saveItem with valid name and stock should return success with positive id`() = runTest {
        val item = createItem(name = "HT Handy Talky", stock = 11)
        val result = saveItemUseCase(item)

        assertTrue(result.isSuccess)
        val id = result.getOrNull()
        assertTrue((id ?: 0L) > 0L)
    }

    // Test 6: Delete item harus menghapus dari repository
    @Test
    fun `deleteItem should remove item from repository`() = runTest {
        val id = repository.insertItem(createItem("To Delete"))
        val result = deleteItemUseCase(id)

        assertTrue(result.isSuccess)

        getAllItemsUseCase().test {
            val items = awaitItem()
            assertTrue(items.none { it.id == id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 7: Search di searchItemsUseCase harus return hasil yang relevan
    @Test
    fun `searchItemsUseCase with category filter should only return matching category`() = runTest {
        repository.insertItem(createItem("Stethoscope", category = ItemCategory.MEDICAL))
        repository.insertItem(createItem("Bread", category = ItemCategory.FOOD))
        repository.insertItem(createItem("Bandage", category = ItemCategory.MEDICAL))

        searchItemsUseCase(query = "", category = ItemCategory.MEDICAL).test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertTrue(items.all { it.category == ItemCategory.MEDICAL })
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 8: getAllItems default sort (UPDATED_DESC) harus berhasil
    @Test
    fun `getAllItems with default sort should return all items`() = runTest {
        repository.insertItem(createItem("Item A"))
        repository.insertItem(createItem("Item B"))

        getAllItemsUseCase().test {
            val items = awaitItem()
            assertEquals(2, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createItem(
        name: String,
        stock: Int = 5,
        category: ItemCategory = ItemCategory.OTHER
    ): Item {
        return Item(
            id = 0L,
            name = name,
            description = "Test description",
            category = category,
            location = "Sekre HMIF",
            totalStock = stock,
            availableStock = stock,
            condition = ItemCondition.GOOD,
            picName = "Test PIC",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}