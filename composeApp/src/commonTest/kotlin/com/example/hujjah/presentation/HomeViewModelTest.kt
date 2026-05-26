package com.example.hujjah.presentation

import app.cash.turbine.test
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.hujjah.data.local.datastore.UserPreferences
import com.example.hujjah.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.Path.Companion.toPath
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: HomeViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Create a temporary preference datastore for testing
        val dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { "build/tmp/test_hujjah_preferences.preferences_pb".toPath() }
        )
        userPreferences = UserPreferences(dataStore)
        viewModel = HomeViewModel(userPreferences)
    }
    
    @AfterTest
    fun tearDown() = runTest {
        Dispatchers.resetMain()
        // Reset/clear user preferences after test
        userPreferences.resetReadingDuration()
    }
    
    @Test
    fun `initial state should have default values`() = runTest(testDispatcher) {
        viewModel.readingDurationSeconds.test {
            assertEquals(0, awaitItem())
        }
        viewModel.currentStreakDays.test {
            assertEquals(0, awaitItem())
        }
        viewModel.lastReadLocation.test {
            assertEquals("", awaitItem())
        }
        viewModel.quoteOfTheDay.test {
            val quote = awaitItem()
            assertNotNull(quote)
            assertEquals("QS. Ar-Ra'd: 28", quote.reference) // Let's check the default quote reference or general fallback
        }
    }
    
    @Test
    fun `simulateReadingTime should update reading duration`() = runTest(testDispatcher) {
        viewModel.simulateReadingTime(60)
        
        // Wait for coroutine to complete
        testScheduler.advanceUntilIdle()
        
        viewModel.readingDurationSeconds.test {
            assertEquals(60, awaitItem())
        }
    }
    
    @Test
    fun `resetReadingTime should set duration to zero`() = runTest(testDispatcher) {
        viewModel.simulateReadingTime(60)
        testScheduler.advanceUntilIdle()
        
        viewModel.resetReadingTime()
        testScheduler.advanceUntilIdle()
        
        viewModel.readingDurationSeconds.test {
            assertEquals(0, awaitItem())
        }
    }
}
