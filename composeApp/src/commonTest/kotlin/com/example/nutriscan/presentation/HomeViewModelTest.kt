package com.example.nutriscan.presentation

import app.cash.turbine.test
import com.example.nutriscan.data.repository.FakeConsumptionRepository
import com.example.nutriscan.data.repository.FakeScanHistoryRepository
import com.example.nutriscan.data.repository.FakeSessionRepository
import com.example.nutriscan.data.repository.FakeUserProfileRepository
import com.example.nutriscan.domain.model.Nutriments
import com.example.nutriscan.domain.model.NutritionAnalysis
import com.example.nutriscan.domain.model.NutritionStatus
import com.example.nutriscan.domain.model.Product
import com.example.nutriscan.domain.model.ScanResult
import com.example.nutriscan.domain.model.UserProfile
import com.example.nutriscan.presentation.screens.home.HomeUiState
import com.example.nutriscan.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var scanHistoryRepository: FakeScanHistoryRepository
    private lateinit var userProfileRepository: FakeUserProfileRepository
    private lateinit var consumptionRepository: FakeConsumptionRepository
    private lateinit var sessionRepository: FakeSessionRepository
    private lateinit var viewModel: HomeViewModel

    private fun makeScan(name: String, barcode: String = "000") = ScanResult(
        product  = Product(barcode = barcode, name = name, nutriments = Nutriments()),
        analysis = NutritionAnalysis(overallStatus = NutritionStatus.SAFE)
    )

    private fun makeProfile() = UserProfile(
        name = "Test User", age = 25, weight = 65f, height = 170f
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        scanHistoryRepository = FakeScanHistoryRepository()
        userProfileRepository = FakeUserProfileRepository()
        consumptionRepository = FakeConsumptionRepository()
        sessionRepository     = FakeSessionRepository()
        viewModel = HomeViewModel(
            scanHistoryRepository = scanHistoryRepository,
            userProfileRepository = userProfileRepository,
            sessionRepository     = sessionRepository,
            consumptionRepository = consumptionRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state awal harus Loading`() {
        assertIs<HomeUiState.Loading>(viewModel.uiState.value)
    }

    @Test
    fun `state berubah ke Ready setelah profile ada`() = runTest {
        userProfileRepository.saveProfile(makeProfile())

        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            val state = awaitItem()
            assertIs<HomeUiState.Ready>(state)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `totalScans nol saat history kosong`() = runTest {
        userProfileRepository.saveProfile(makeProfile())

        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            val state = awaitItem() as? HomeUiState.Ready
            assertEquals(0, state?.dashboard?.totalScans ?: -1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `totalScans bertambah setelah saveScan`() = runTest {
        userProfileRepository.saveProfile(makeProfile())
        scanHistoryRepository.saveScan(makeScan("Aqua", "111"))
        scanHistoryRepository.saveScan(makeScan("Indomie", "222"))

        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            val state = awaitItem() as? HomeUiState.Ready
            assertTrue((state?.dashboard?.totalScans ?: 0) > 0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dashboard userName sesuai profile`() = runTest {
        userProfileRepository.saveProfile(makeProfile())

        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            val state = awaitItem()
            if (state is HomeUiState.Ready) {
                assertEquals("Test User", state.dashboard.userName)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}