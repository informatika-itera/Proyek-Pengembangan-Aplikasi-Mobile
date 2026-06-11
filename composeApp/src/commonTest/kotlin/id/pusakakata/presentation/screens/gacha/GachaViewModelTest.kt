package id.pusakakata.presentation.screens.gacha

import id.pusakakata.data.repository.FakeItemRepository
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.model.Rarity
import id.pusakakata.domain.usecase.GachaSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class GachaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private val testCards = listOf(
        LegendaryCard("1", "Test", "Desc", Rarity.COMMON, "", "Origin")
    )
    private val gachaSystem = GachaSystem(testCards)
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: GachaViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeItemRepository()
        viewModel = GachaViewModel(gachaSystem, repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(GachaUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun drawCard_noTokens_showsError() = runTest {
        viewModel.drawCard()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is GachaUiState.Error)
    }

    @Test
    fun drawCard_withTokens_updatesStateToResult() = runTest {
        repository.addTokens(10)
        viewModel.drawCard()
        
        // Move to Drawing state
        testDispatcher.scheduler.advanceTimeBy(500)
        assertEquals(GachaUiState.Drawing, viewModel.uiState.value)
        
        // Move to Result state after 1500ms
        testDispatcher.scheduler.advanceTimeBy(1100)
        assertTrue(viewModel.uiState.value is GachaUiState.Result)
    }

    @Test
    fun reset_updatesStateToIdle() = runTest {
        repository.addTokens(10)
        viewModel.drawCard()
        testDispatcher.scheduler.advanceTimeBy(2000)
        
        viewModel.reset()
        assertEquals(GachaUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun drawCard_savesToCollection() = runTest {
        repository.addTokens(10)
        viewModel.drawCard()
        
        testDispatcher.scheduler.advanceTimeBy(2000)
        advanceUntilIdle()
        
        val collected = repository.getCollectedCardIds().first()
        assertEquals(1, collected.size)
        assertEquals("1", collected.first())
    }
}
