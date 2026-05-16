package com.example.Roomie.presentation.report

import com.example.Roomie.data.repository.FakeReportRepository
import com.example.Roomie.domain.model.UrgencyLevel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReportViewModelTest {
    private lateinit var repository: FakeReportRepository
    private lateinit var viewModel: ReportViewModel

    @BeforeTest
    fun setup() {
        repository = FakeReportRepository()
        viewModel = ReportViewModel(repository)
    }

    @Test
    fun `initial state should be empty and submit disabled`() {
        val state = viewModel.state.value
        assertEquals("", state.category)
        assertEquals("", state.location)
        assertEquals("", state.description)
        assertFalse(state.isSubmitEnabled)
    }

    @Test
    fun `isSubmitEnabled should be true when all required fields are filled`() {
        viewModel.onCategoryChange("Lab")
        viewModel.onLocationChange("GKU 2")
        viewModel.onDescriptionChange("Rusak")
        
        assertTrue(viewModel.state.value.isSubmitEnabled)
    }

    @Test
    fun `isSubmitEnabled should be false if one required field is empty`() {
        viewModel.onCategoryChange("Lab")
        viewModel.onLocationChange("")
        viewModel.onDescriptionChange("Rusak")
        
        assertFalse(viewModel.state.value.isSubmitEnabled)
    }

    @Test
    fun `urgency change should update state`() {
        viewModel.onUrgencyChange(UrgencyLevel.HIGH)
        assertEquals(UrgencyLevel.HIGH, viewModel.state.value.urgency)
    }
}
