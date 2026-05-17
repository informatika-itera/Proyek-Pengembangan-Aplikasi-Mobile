package id.pusakakata.ui.screens.gacha

import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.model.Rarity
import id.pusakakata.domain.usecase.GachaSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class GachaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private val testCards = listOf(
        LegendaryCard("1", "Test", "Desc", Rarity.COMMON, "", "Origin")
    )
    private val gachaSystem = GachaSystem(testCards)
    private lateinit var viewModel: GachaViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GachaViewModel(gachaSystem)
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
    fun drawCard_updatesStateToResult() {
        viewModel.drawCard()
        
        // Move to Drawing state
        testDispatcher.scheduler.advanceTimeBy(500)
        assertEquals(GachaUiState.Drawing, viewModel.uiState.value)
        
        // Move to Result state after 1500ms
        testDispatcher.scheduler.advanceTimeBy(1100)
        assertTrue(viewModel.uiState.value is GachaUiState.Result)
    }

    @Test
    fun reset_updatesStateToIdle() {
        viewModel.drawCard()
        testDispatcher.scheduler.advanceTimeBy(2000)
        
        viewModel.reset()
        assertEquals(GachaUiState.Idle, viewModel.uiState.value)
    }
}
