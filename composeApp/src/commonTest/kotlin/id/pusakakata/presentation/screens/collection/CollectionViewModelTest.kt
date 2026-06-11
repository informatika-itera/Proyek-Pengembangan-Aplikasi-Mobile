package id.pusakakata.presentation.screens.collection

import id.pusakakata.data.repository.FakeItemRepository
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.model.Rarity
import id.pusakakata.domain.usecase.GachaSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var gachaSystem: GachaSystem
    private lateinit var viewModel: CollectionViewModel

    private val testCards = listOf(
        LegendaryCard("1", "Card 1", "Desc", Rarity.COMMON, "", "Origin"),
        LegendaryCard("2", "Card 2", "Desc", Rarity.RARE, "", "Origin")
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeItemRepository()
        gachaSystem = GachaSystem(testCards)
        viewModel = CollectionViewModel(gachaSystem, repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasAllCards() = runTest {
        val job = backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()
        
        assertEquals(testCards.size, viewModel.uiState.value.allCards.size)
        assertTrue(viewModel.uiState.value.collectedIds.isEmpty())
        job.cancel()
    }

    @Test
    fun collectedCards_updatesWhenRepositoryChanges() = runTest {
        val job = backgroundScope.launch { viewModel.uiState.collect {} }
        repository.saveCollectedCard("1")
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value.collectedIds.contains("1"))
        assertEquals(1, viewModel.uiState.value.collectedIds.size)
        job.cancel()
    }

    @Test
    fun initialState_loadsCollectedCards() = runTest {
        repository.saveCollectedCard("1")
        
        val viewModel2 = CollectionViewModel(gachaSystem, repository)
        val job = backgroundScope.launch { viewModel2.uiState.collect {} }
        advanceUntilIdle()
        
        assertTrue(viewModel2.uiState.value.collectedIds.contains("1"))
        job.cancel()
    }
}
