package id.pusakakata.presentation.screens.profile

import id.pusakakata.data.repository.FakeItemRepository
import id.pusakakata.domain.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: ProfileViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeItemRepository()
        viewModel = ProfileViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun stats_updateCorrectly() = runTest {
        backgroundScope.launch { viewModel.totalWords.collect {} }
        backgroundScope.launch { viewModel.favoriteCount.collect {} }
        backgroundScope.launch { viewModel.tokens.collect {} }
        
        repository.insertWord(Word("1", "T1", "D1", "C1"))
        repository.insertWord(Word("2", "T2", "D2", "C1", isFavorite = true))
        repository.addTokens(20)
        
        advanceUntilIdle()
        
        assertEquals(2, viewModel.totalWords.value)
        assertEquals(1, viewModel.favoriteCount.value)
        assertEquals(20L, viewModel.tokens.value)
    }
}
