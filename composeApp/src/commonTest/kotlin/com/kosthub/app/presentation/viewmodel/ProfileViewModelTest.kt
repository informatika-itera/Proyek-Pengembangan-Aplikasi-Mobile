package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.domain.model.Profile
import com.kosthub.app.presentation.state.OperationState
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.testing.FakeProfileRepository
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = FakeProfileRepository()
    private lateinit var viewModel: ProfileViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        viewModel.dispose()
    }

    @Test
    fun testInitialLoadingAndLoadSuccess() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals("Anonim", (state as UiState.Success).data.name)
    }

    @Test
    fun testSaveProfileUpdatesUIStateAndRepository() = runTest(testDispatcher) {
        val updatedProfile = Profile(1L, "Qoriib", "qoriib@kosthub.com", -5.358, 105.314)
        viewModel.saveProfile(updatedProfile)

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals("Qoriib", (state as UiState.Success).data.name)

        assertEquals("Qoriib", repository.getProfile()?.name)

        assertTrue(viewModel.operationState.value is OperationState.Success)
    }

    @Test
    fun testClearProfileResetsToDefault() = runTest(testDispatcher) {
        val customProfile = Profile(1L, "Qoriib", "qoriib@kosthub.com", -5.358, 105.314)
        viewModel.saveProfile(customProfile)
        assertEquals("Qoriib", repository.getProfile()?.name)

        viewModel.clearProfile()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals("Anonim", (state as UiState.Success).data.name)
        assertEquals("Anonim", repository.getProfile()?.name)
    }
}
