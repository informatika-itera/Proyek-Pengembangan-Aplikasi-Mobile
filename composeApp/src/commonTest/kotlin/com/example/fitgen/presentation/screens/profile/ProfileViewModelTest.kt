package com.example.fitgen.presentation.screens.profile

import com.example.fitgen.core.util.MainDispatcherRule
import com.example.fitgen.data.local.datastore.UserPreferences
import com.example.fitgen.data.repository.FakeDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDataStore: FakeDataStore
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        fakeDataStore = FakeDataStore()
        userPreferences = UserPreferences(fakeDataStore)
        viewModel = ProfileViewModel(userPreferences)
    }

    @Test
    fun `test onAgeChange with valid input updates state without error`() = runTest {
        viewModel.onAgeChange("25")
        
        val state = viewModel.uiState.value
        assertEquals("25", state.age)
        assertNull(state.ageError)
    }

    @Test
    fun `test onAgeChange with over 120 triggers ageError`() = runTest {
        viewModel.onAgeChange("150")
        
        val state = viewModel.uiState.value
        assertEquals("150", state.age)
        assertNotNull(state.ageError)
        assertEquals("Umur maksimal 120", state.ageError)
    }

    @Test
    fun `test onWeightChange with valid input updates state`() = runTest {
        viewModel.onWeightChange("70.5")
        
        val state = viewModel.uiState.value
        assertEquals("70.5", state.weight)
        assertNull(state.weightError)
    }

    @Test
    fun `test onWeightChange with over 500kg triggers weightError`() = runTest {
        viewModel.onWeightChange("600")
        
        val state = viewModel.uiState.value
        assertEquals("600", state.weight)
        assertNotNull(state.weightError)
        assertEquals("Berat badan maksimal 500 kg", state.weightError)
    }

    @Test
    fun `test onHeightChange with over 300cm triggers heightError`() = runTest {
        viewModel.onHeightChange("350")
        
        val state = viewModel.uiState.value
        assertEquals("350", state.height)
        assertNotNull(state.heightError)
        assertEquals("Tinggi badan maksimal 300 cm", state.heightError)
    }

    @Test
    fun `test executeSaveProfile with valid data saves to DataStore`() = runTest {
        // Given valid input
        viewModel.onNameChange("Budi")
        viewModel.onAgeChange("25")
        viewModel.onWeightChange("70")
        viewModel.onHeightChange("175")
        viewModel.onGenderChange("Laki-laki")

        // When
        viewModel.saveProfile()

        // Then
        val profile = userPreferences.userProfile.first()
        assertEquals("Budi", profile.name)
        assertEquals(25, profile.age)
        assertEquals(70.0, profile.weightKg)
        assertEquals(175.0, profile.heightCm)
    }

    @Test
    fun `test executeSaveProfile with invalid data prevents saving`() = runTest {
        // Given existing data in repository
        userPreferences.saveUserProfile(
            com.example.fitgen.domain.model.UserProfile(name = "Original", age = 20, gender = "Pria", heightCm = 170.0, weightKg = 50.0, goal = "Sehat")
        )
        
        // When updating with invalid data
        viewModel.onNameChange("Budi")
        viewModel.onWeightChange("600") // Error
        viewModel.saveProfile()

        // Then the repository data should not be updated because of validation failure
        val profile = userPreferences.userProfile.first()
        assertEquals("Original", profile.name)
        assertEquals(50.0, profile.weightKg)
    }
}
