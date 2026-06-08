package com.example.nutriscan.presentation.screens.onboarding

import app.cash.turbine.test
import com.example.nutriscan.data.local.datastore.InMemoryDataStore
import com.example.nutriscan.data.local.datastore.UserPreferences
import com.example.nutriscan.data.repository.FakeUserProfileRepository
import com.example.nutriscan.domain.model.Disease
import com.example.nutriscan.domain.usecase.SaveUserProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelCommonTest {

    private lateinit var viewModel: OnboardingViewModel
    private lateinit var fakeRepository: FakeUserProfileRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeUserProfileRepository()

        viewModel = OnboardingViewModel(
            saveUserProfileUseCase = SaveUserProfileUseCase(fakeRepository),
            userPreferences = UserPreferences(InMemoryDataStore())
        )
    }

    @AfterTest
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
    fun `form valid setelah semua field diisi dengan benar`() {
        viewModel.onNameChange("Budi")
        viewModel.onAgeChange("25")
        viewModel.onWeightChange("70")
        viewModel.onHeightChange("170")

        assertTrue(viewModel.form.value.isValid)
    }

    @Test
    fun `nama kosong menghasilkan error`() {
        viewModel.onNameChange("")

        assertEquals(
            expected = "Nama tidak boleh kosong",
            actual = viewModel.form.value.nameError
        )
    }

    @Test
    fun `usia di luar range menghasilkan error`() {
        viewModel.onAgeChange("150")

        assertEquals(
            expected = "Usia harus antara 1–120 tahun",
            actual = viewModel.form.value.ageError
        )
    }

    @Test
    fun `berat badan bukan angka menghasilkan error`() {
        viewModel.onWeightChange("abc")

        assertEquals(
            expected = "Berat badan harus berupa angka",
            actual = viewModel.form.value.weightError
        )
    }

    @Test
    fun `tinggi badan di luar range menghasilkan error`() {
        viewModel.onHeightChange("400")

        assertEquals(
            expected = "Tinggi badan tidak valid",
            actual = viewModel.form.value.heightError
        )
    }

    @Test
    fun `toggle disease menambah dan menghapus kondisi`() {
        viewModel.onDiseaseToggled(Disease.DIABETES)
        assertTrue(Disease.DIABETES in viewModel.form.value.selectedDiseases)

        viewModel.onDiseaseToggled(Disease.DIABETES)
        assertFalse(Disease.DIABETES in viewModel.form.value.selectedDiseases)
    }

    @Test
    fun `saveProfile sukses menghasilkan state Success`() = runTest {
        viewModel.onNameChange("Budi")
        viewModel.onAgeChange("25")
        viewModel.onWeightChange("70")
        viewModel.onHeightChange("170")

        viewModel.uiState.test {
            assertEquals(OnboardingUiState.Idle, awaitItem())

            viewModel.saveProfile()
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState is OnboardingUiState.Loading)

            val successState = awaitItem()
            assertTrue(successState is OnboardingUiState.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProfile tidak berjalan jika form tidak valid`() = runTest {
        viewModel.saveProfile()

        assertEquals(
            expected = OnboardingUiState.Idle,
            actual = viewModel.uiState.value
        )
    }
}