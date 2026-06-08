package com.example.fitkos.presentation.screens.dashboard

import com.example.fitkos.createTestDataStore
import com.example.fitkos.data.local.datastore.UserPreferences
import com.example.fitkos.data.repository.FakeNoteRepository
import com.example.fitkos.data.repository.FakeWaterRepository
import com.example.fitkos.domain.model.Note
import com.example.fitkos.domain.model.NoteCategory
import com.example.fitkos.domain.model.NoteColor
import com.example.fitkos.domain.model.WaterLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var waterRepository: FakeWaterRepository
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: DashboardViewModel

    private val todayDate: String
        get() {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')}"
        }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        noteRepository = FakeNoteRepository()
        waterRepository = FakeWaterRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupViewModel(testScope: kotlinx.coroutines.test.TestScope) {
        val testDataStore = createTestDataStore(testScope.backgroundScope)
        userPreferences = UserPreferences(testDataStore)
        viewModel = DashboardViewModel(
            repository = noteRepository,
            waterRepository = waterRepository,
            userPreferences = userPreferences
        )
    }

    @Test
    fun `initial state should reflect repositories data`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote("Lunch"))
        waterRepository.upsertWaterLog(WaterLog(todayDate, 5, 8))

        setupViewModel(this)

        // Assert
        // Tunggu sampai Flow benar-benar mengirim state yang sudah berisi data repository.
        val state = viewModel.uiState.first {
            it.mealCount == 1 && it.waterGlasses == 5
        }

        assertEquals(1, state.mealCount)
        assertEquals(5, state.waterGlasses)
        assertEquals(8, state.waterTarget)
    }

    private fun createTestNote(title: String): Note {
        return Note(
            id = 0,
            title = title,
            content = "Test",
            category = NoteCategory.LUNCH,
            color = NoteColor.DEFAULT,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}