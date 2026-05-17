package com.soundletter.app.presentation.screens.search

import app.cash.turbine.test
import com.soundletter.app.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeMusicRepository : MusicRepository {
    override suspend fun searchSongs(query: String): List<String> {
        return if (query == "The Weeknd") listOf("Starboy", "After Hours") else emptyList()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SearchScreenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeMusicRepository
    private lateinit var viewModel: SearchScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMusicRepository()
        viewModel = SearchScreenViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onQueryChange should update query state`() = runTest {
        val newQuery = "Taylor Swift"
        
        viewModel.state.test {
            // Initial state
            assertEquals("", awaitItem().query)

            // Update query
            viewModel.onQueryChange(newQuery)

            // Verify state
            assertEquals(newQuery, awaitItem().query)
        }
    }

    @Test
    fun `blank query should clear results`() = runTest {
        viewModel.onQueryChange("")
        
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(true, state.results.isEmpty())
            assertEquals(false, state.isLoading)
        }
    }
}
