package com.example.fitgen.presentation.screens.ai

import com.example.fitgen.core.util.MainDispatcherRule
import com.example.fitgen.data.repository.FakeAIRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AIAssistantViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var aiRepository: FakeAIRepository
    private lateinit var viewModel: AIAssistantViewModel

    @Before
    fun setUp() {
        aiRepository = FakeAIRepository()
        viewModel = AIAssistantViewModel(aiRepository)
    }

    @Test
    fun `test executeAction with empty input shows error`() = runTest {
        // Given empty input text
        viewModel.onInputTextChange("   ")
        
        // When
        viewModel.executeAction()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("Masukkan teks terlebih dahulu", state.error)
        assertFalse(state.isLoading)
        assertNull(state.result)
    }

    @Test
    fun `test executeAction success updates result state`() = runTest {
        // Given valid input and action
        viewModel.onInputTextChange("Hello World")
        viewModel.onActionSelected(AIAction.SUMMARIZE)
        aiRepository.shouldReturnError = false
        
        // When
        viewModel.executeAction()
        
        // Then
        val state = viewModel.uiState.value
        assertNull(state.error)
        assertFalse(state.isLoading)
        assertEquals("Summary of: Hello World", state.result)
    }

    @Test
    fun `test executeAction failure updates error state`() = runTest {
        // Given valid input but network fails
        viewModel.onInputTextChange("Hello World")
        aiRepository.shouldReturnError = true
        aiRepository.customErrorMessage = "Tidak ada koneksi internet. Silakan periksa jaringan Anda."
        
        // When
        viewModel.executeAction()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.result)
        assertEquals("Tidak ada koneksi internet. Silakan periksa jaringan Anda.", state.error)
    }
}
