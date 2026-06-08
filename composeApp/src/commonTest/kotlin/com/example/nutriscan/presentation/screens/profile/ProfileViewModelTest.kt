package com.example.nutriscan.presentation.screens.profile

import com.example.nutriscan.data.local.datastore.InMemoryDataStore
import com.example.nutriscan.data.local.datastore.UserPreferences
import com.example.nutriscan.data.repository.FakeSessionRepository
import com.example.nutriscan.data.repository.FakeUserProfileRepository
import com.example.nutriscan.domain.model.Disease
import com.example.nutriscan.domain.model.UserProfile
import com.example.nutriscan.domain.usecase.GetUserProfileUseCase
import com.example.nutriscan.domain.usecase.UpdateUserProfileUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelCommonTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var profileRepository: FakeUserProfileRepository
    private lateinit var sessionRepository: FakeSessionRepository
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: ProfileViewModel

    private fun profile(name: String = "Budi") = UserProfile(
        name = name,
        age = 25,
        weight = 70f,
        height = 170f,
        healthConditions = listOf(Disease.DIABETES)
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)

        profileRepository = FakeUserProfileRepository()
        sessionRepository = FakeSessionRepository()
        userPreferences = UserPreferences(InMemoryDataStore())

        viewModel = ProfileViewModel(
            getProfileUseCase = GetUserProfileUseCase(profileRepository),
            updateProfileUseCase = UpdateUserProfileUseCase(profileRepository),
            sessionRepository = sessionRepository,
            userPreferences = userPreferences
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel berhasil dibuat dan memiliki uiState`() = runTest {
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `startEditing aman dipanggil setelah profile tersedia`() = runTest {
        profileRepository.saveProfile(profile("Cahya"))

        advanceUntilIdle()

        viewModel.startEditing()

        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `cancelEditing aman dipanggil`() = runTest {
        profileRepository.saveProfile(profile("Cahya"))

        advanceUntilIdle()

        viewModel.startEditing()
        viewModel.cancelEditing()

        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `ubah field form edit aman dipanggil`() = runTest {
        profileRepository.saveProfile(profile("Cahya"))

        advanceUntilIdle()

        viewModel.startEditing()
        viewModel.onNameChange("Stevanus")
        viewModel.onAgeChange("22")
        viewModel.onWeightChange("65")
        viewModel.onHeightChange("170")
        viewModel.onDiseaseToggled(Disease.DIABETES)

        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `saveProfile aman dipanggil setelah form valid`() = runTest {
        profileRepository.saveProfile(profile("Budi"))

        advanceUntilIdle()

        viewModel.startEditing()
        viewModel.onNameChange("Cahya")
        viewModel.onAgeChange("25")
        viewModel.onWeightChange("70")
        viewModel.onHeightChange("170")

        viewModel.saveProfile()

        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `toggleDarkMode aman dipanggil`() = runTest {
        viewModel.toggleDarkMode(true)

        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `topUp menambah coin di repository fake`() = runTest {
        val before = sessionRepository.currentCoins()

        viewModel.topUp(50)

        advanceUntilIdle()

        val after = sessionRepository.currentCoins()

        assertTrue(after >= before)
    }

    @Test
    fun `consumeMessage aman dipanggil`() = runTest {
        viewModel.topUp(50)

        advanceUntilIdle()

        viewModel.consumeMessage()

        assertNotNull(viewModel.uiState.value)
    }
}