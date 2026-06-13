package com.example.noteai.presentation.screens.pantry

import app.cash.turbine.test
import com.example.noteai.domain.model.PantryItem
import com.example.noteai.domain.repository.PantryRepository
import com.example.noteai.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PantryViewModelTest {

    private lateinit var viewModel: PantryViewModel
    private lateinit var repository: FakePantryRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePantryRepository()
        
        viewModel = PantryViewModel(
            getPantryItems = GetPantryItems(repository),
            addPantryItem = AddPantryItem(repository),
            updatePantryItem = UpdatePantryItem(repository),
            deletePantryItem = DeletePantryItem(repository),
            updateStockAmount = UpdateStockAmount(repository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial items should be empty`() = runTest {
        viewModel.pantryItems.test {
            assertEquals(emptyList(), awaitItem())
        }
    }

    @Test
    fun `search should filter items`() = runTest {
        repository.addItem(createItem("Beras"))
        repository.addItem(createItem("Gula"))

        viewModel.onSearchQueryChange("Beras")

        viewModel.pantryItems.test {
            // Initial emit might be empty or previous state, wait for debounced search
            val items = awaitItem()
            if (items.isEmpty()) {
                val nextItems = awaitItem()
                assertEquals(1, nextItems.size)
                assertEquals("Beras", nextItems.first().name)
            } else {
                assertEquals(1, items.size)
                assertEquals("Beras", items.first().name)
            }
        }
    }

    @Test
    fun `category selection should filter items`() = runTest {
        repository.addItem(createItem("Daging Sapi", category = "Daging"))
        repository.addItem(createItem("Bayam", category = "Sayuran"))

        viewModel.onCategorySelected("Daging")

        viewModel.pantryItems.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Daging", items.first().category)
        }
    }

    private fun createItem(name: String, category: String = "Bahan Utama") = PantryItem(
        id = 0,
        name = name,
        amount = 10.0,
        unit = "kg",
        category = category,
        minStock = 2.0
    )
}

class FakePantryRepository : PantryRepository {
    private val items = MutableStateFlow<List<PantryItem>>(emptyList())

    override fun getAllPantryItems(): Flow<List<PantryItem>> = items

    override suspend fun insertPantryItem(item: PantryItem) {
        items.value += item.copy(id = items.value.size.toLong() + 1)
    }

    override suspend fun updatePantryItem(item: PantryItem) {
        items.value = items.value.map { if (it.id == item.id) item else it }
    }

    override suspend fun deletePantryItem(id: Long) {
        items.value = items.value.filter { it.id != id }
    }

    override suspend fun updateStockAmount(id: Long, amount: Double) {
        items.value = items.value.map { if (it.id == id) it.copy(amount = amount) else it }
    }
    
    // Helper for testing
    suspend fun addItem(item: PantryItem) = insertPantryItem(item)
}
