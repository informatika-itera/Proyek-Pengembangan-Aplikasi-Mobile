package com.example.nutriscan.presentation.screens.onboarding

import app.cash.turbine.test
import com.example.nutriscan.data.local.datastore.UserPreferences
import com.example.nutriscan.data.repository.FakeUserProfileRepository
import com.example.nutriscan.domain.model.Disease
import com.example.nutriscan.domain.usecase.SaveUserProfileUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel
    private lateinit var fakeRepository: FakeUserProfileRepository
    private val mockPreferences = mockk<UserPreferences>(relaxed = true)
    private val testDispatcher  = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeUserProfileRepository()
        // Mock flows yang di-observe UserPreferences
        coEvery { mockPreferences.isOnboardingCompleted } returns flowOf(false)
        coEvery { mockPreferences.setOnboardingCompleted() } returns Unit

        viewModel = OnboardingViewModel(
            saveUserProfileUseCase = SaveUserProfileUseCase(fakeRepository),
            userPreferences        = mockPreferences
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state awal harus Idle`() {
        assertTrue(viewModel.uiState.value is OnboardingUiState.Idle)
    }

    @Test
    fun `form awal tidak valid`() {
        assertFalse(viewModel.form.value.isValid)
    }

    @Test
    fun `form valid setelah semua field diisi benar`() {
        viewModel.onNameChange("Budi Santoso")
        viewModel.onAgeChange("25")
        viewModel.onWeightChange("70")
        viewModel.onHeightChange("170")

        assertTrue(viewModel.form.value.isValid)
    }

    @Test
    fun `nama kosong menghasilkan nameError`() {
        viewModel.onNameChange("")
        assertEquals("Nama tidak boleh kosong", viewModel.form.value.nameError)
    }

    @Test
    fun `usia 0 menghasilkan ageError range`() {
        viewModel.onAgeChange("0")
        assertEquals("Usia harus antara 1–120 tahun", viewModel.form.value.ageError)
    }

    @Test
    fun `usia bukan angka menghasilkan ageError format`() {
        viewModel.onAgeChange("dua puluh")
        assertEquals("Usia harus berupa angka", viewModel.form.value.ageError)
    }

    @Test
    fun `toggle disease dua kali harus kembali ke kondisi awal`() {
        viewModel.onDiseaseToggled(Disease.DIABETES)
        assertTrue(Disease.DIABETES in viewModel.form.value.selectedDiseases)

        viewModel.onDiseaseToggled(Disease.DIABETES)
        assertFalse(Disease.DIABETES in viewModel.form.value.selectedDiseases)
    }

    @Test
    fun `saveProfile tidak jalan jika form tidak valid`() = runTest {
        viewModel.saveProfile()
        assertEquals(OnboardingUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `saveProfile sukses mengubah state ke Success`() = runTest {
        viewModel.onNameChange("Budi Santoso")
        viewModel.onAgeChange("25")
        viewModel.onWeightChange("70")
        viewModel.onHeightChange("170")

        viewModel.uiState.test {
            assertEquals(OnboardingUiState.Idle, awaitItem())
            viewModel.saveProfile()
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is OnboardingUiState.Loading)
            assertTrue(awaitItem() is OnboardingUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resetError mengembalikan state ke Idle`() {
        viewModel.onNameChange("") // trigger, tapi state tetap Idle
        viewModel.resetError()
        assertTrue(viewModel.uiState.value is OnboardingUiState.Idle)
    }
}