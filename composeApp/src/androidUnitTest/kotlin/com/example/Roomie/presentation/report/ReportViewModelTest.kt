package com.example.Roomie.presentation.report

import com.example.Roomie.data.remote.SupabaseService
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.domain.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ReportViewModel
    private val mockReportRepo = mockk<ReportRepository>(relaxed = true)
    private val mockSupabase = mockk<SupabaseService>(relaxed = true)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ReportViewModel(mockReportRepo, mockSupabase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `input changes should update state`() {
        viewModel.onCategoryChange("Fasilitas")
        viewModel.onLocationChange("GKU2")
        viewModel.onDescriptionChange("AC Mati")
        viewModel.onUrgencyChange(UrgencyLevel.HIGH)
        
        val state = viewModel.state.value
        assertEquals("Fasilitas", state.category)
        assertEquals("GKU2", state.location)
        assertEquals("AC Mati", state.description)
        assertEquals(UrgencyLevel.HIGH, state.urgency)
    }

    @Test
    fun `submitReport success scenario`() = runTest {
        viewModel.onCategoryChange("Fasilitas")
        viewModel.onLocationChange("GKU2")
        viewModel.onDescriptionChange("AC Mati")
        
        coEvery { mockSupabase.uploadReportImage(any()) } returns "https://test.com/img.jpg"
        viewModel.onImagePicked(byteArrayOf(1, 2, 3))
        
        viewModel.submitReport()
        
        assertTrue(viewModel.state.value.isSubmitted)
        assertFalse(viewModel.state.value.isLoading)
        coVerify { mockReportRepo.submitReport(any()) }
    }

    @Test
    fun `submitReport fail scenario - upload error`() = runTest {
        viewModel.onCategoryChange("Fasilitas")
        viewModel.onLocationChange("GKU2")
        viewModel.onDescriptionChange("AC Mati")
        
        coEvery { mockSupabase.uploadReportImage(any()) } returns null
        viewModel.onImagePicked(byteArrayOf(1, 2, 3))
        
        viewModel.submitReport()
        
        assertNotNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSubmitted)
    }
}
