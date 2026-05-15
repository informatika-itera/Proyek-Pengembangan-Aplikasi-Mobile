package com.dailybliss.app.presentation

import app.cash.turbine.test
import com.dailybliss.app.data.repository.FakeMomentRepository
import com.dailybliss.app.domain.model.Moment
import com.dailybliss.app.domain.usecase.GetAllMomentsUseCase
import com.dailybliss.app.domain.usecase.MomentSortBy
import com.dailybliss.app.domain.usecase.SaveMomentUseCase
import com.dailybliss.app.domain.usecase.SearchMomentsUseCase
import com.dailybliss.app.presentation.screens.home.DashboardViewModel
import com.dailybliss.app.presentation.screens.home.HomeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit Tests untuk DashboardViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: FakeMomentRepository
    private lateinit var getAllMomentsUseCase: GetAllMomentsUseCase
    private lateinit var searchMomentsUseCase: SearchMomentsUseCase
    private lateinit var saveMomentUseCase: SaveMomentUseCase
    private lateinit var viewModel: DashboardViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repository = FakeMomentRepository()
        getAllMomentsUseCase = GetAllMomentsUseCase(repository)
        searchMomentsUseCase = SearchMomentsUseCase(repository)
        saveMomentUseCase = SaveMomentUseCase(repository)
        
        viewModel = DashboardViewModel(
            getAllMomentsUseCase = getAllMomentsUseCase,
            searchMomentsUseCase = searchMomentsUseCase,
            saveMomentUseCase = saveMomentUseCase
        )
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        viewModel.uiState.test {
            // Initial loading state
            val loading = awaitItem()
            assertTrue(loading is HomeUiState.Loading)
            
            // After loading, should be empty (no moments)
            advanceUntilIdle()
            val empty = awaitItem()
            assertTrue(empty is HomeUiState.Empty)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `state should be Success when moments exist`() = runTest {
        // Arrange
        repository.insertMoment(createTestMoment("Moment 1"))
        repository.insertMoment(createTestMoment("Moment 2"))
        
        // Create new viewmodel after inserting moments
        val vm = DashboardViewModel(
            getAllMomentsUseCase = getAllMomentsUseCase,
            searchMomentsUseCase = searchMomentsUseCase,
            saveMomentUseCase = saveMomentUseCase
        )
        
        // Act & Assert
        vm.uiState.test {
            skipItems(1) // Skip loading
            advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, (state as HomeUiState.Success).moments.size)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `search should filter moments by query`() = runTest {
        // Arrange
        repository.insertMoment(createTestMoment("Kotlin Guide"))
        repository.insertMoment(createTestMoment("Java Tutorial"))
        
        val vm = DashboardViewModel(
            getAllMomentsUseCase = getAllMomentsUseCase,
            searchMomentsUseCase = searchMomentsUseCase,
            saveMomentUseCase = saveMomentUseCase
        )
        
        vm.uiState.test {
            skipItems(1) // Skip loading
            advanceUntilIdle()
            skipItems(1) // Skip initial success
            
            // Act
            vm.onSearchQueryChange("Kotlin")
            advanceUntilIdle()
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).moments.size)
            assertEquals("Kotlin Guide", state.moments.first().title)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    private fun createTestMoment(title: String): Moment {
        return Moment(
            id = 0,
            title = title,
            content = "Test content",
            imageUrl = null,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}
