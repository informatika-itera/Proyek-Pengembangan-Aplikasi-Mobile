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
import kotlinx.datetime.Clock
import kotlinx.coroutines.cancelChildren
import androidx.lifecycle.viewModelScope
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
        
        // Buat file preference unik untuk setiap test run agar tidak bentrok (IllegalStateException di OkioStorage)
        val uniqueName = "test_hujjah_pref_${Clock.System.now().toEpochMilliseconds()}_${(0..99999).random()}.preferences_pb"
        val dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { ("build/tmp/" + uniqueName).toPath() }
        )
        userPreferences = UserPreferences(dataStore)
        viewModel = HomeViewModel(userPreferences)
    }
    
    @AfterTest
    fun tearDown() {
        viewModel.viewModelScope.coroutineContext.cancelChildren()
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should have default values`() = runTest(testDispatcher) {
        viewModel.readingDurationSeconds.test {
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.currentStreakDays.test {
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.lastReadLocation.test {
            assertEquals("", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.quoteOfTheDay.test {
            val quote = awaitItem()
            assertNotNull(quote)
            assertEquals("QS. Ar-Ra'd: 28", quote.reference)
            cancelAndIgnoreRemainingEvents()
        }
        testScheduler.advanceTimeBy(6000)
    }
    
    @Test
    fun `simulateReadingTime should update reading duration`() = runTest(testDispatcher) {
        userPreferences.addReadingDuration(60)
        testScheduler.advanceUntilIdle()
        
        viewModel.readingDurationSeconds.test {
            assertEquals(0, awaitItem()) // initial value
            assertEquals(60, awaitItem()) // updated value
            cancelAndIgnoreRemainingEvents()
        }
        testScheduler.advanceTimeBy(6000)
    }
    
    @Test
    fun `resetReadingTime should set duration to zero`() = runTest(testDispatcher) {
        userPreferences.addReadingDuration(60)
        testScheduler.advanceUntilIdle()
        
        viewModel.resetReadingTime()
        testScheduler.advanceUntilIdle()
        
        viewModel.readingDurationSeconds.test {
            assertEquals(0, awaitItem()) // Karena disubscribe setelah reset, langsung bernilai 0
            cancelAndIgnoreRemainingEvents()
        }
        testScheduler.advanceTimeBy(6000)
    }
}
