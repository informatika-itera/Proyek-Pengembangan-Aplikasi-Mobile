package com.studymate.presentation

import app.cash.turbine.test
import com.studymate.data.repository.FakeNoteRepository
import com.studymate.domain.model.Note
import com.studymate.domain.model.UserProfile
import com.studymate.domain.repository.MantraRepository
import com.studymate.domain.repository.NoteRepository
import com.studymate.domain.repository.UserProfileRepository
import com.studymate.presentation.screens.home.HomeUiState
import com.studymate.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var noteRepository: NoteRepository
    private lateinit var profileRepository: FakeUserProfileRepository
    private lateinit var mantraRepository: FakeMantraRepository
    private lateinit var viewModel: HomeViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        noteRepository = FakeNoteRepository()
        profileRepository = FakeUserProfileRepository()
        mantraRepository = FakeMantraRepository()
        
        viewModel = HomeViewModel(
            noteRepository = noteRepository,
            profileRepository = profileRepository,
            mantraRepository = mantraRepository
        )
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be Loading then Success`() = runTest {
        viewModel.uiState.test {
            // Initial loading state
            assertTrue(awaitItem() is HomeUiState.Loading)
            
            advanceUntilIdle()
            
            // After loading, should be success (with default values)
            assertTrue(awaitItem() is HomeUiState.Success)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refresh mantra should update profile with db mantra`() = runTest {
        profileRepository.saveProfile(
            UserProfile(
                id = 1,
                localName = "Budi",
                nim = "123",
                currentStreak = 2,
                dailyMantra = "Jangan pernah berhenti belajar, karena hidup tidak pernah berhenti mengajar."
            )
        )

        advanceUntilIdle()

        viewModel.refreshMantra()
        advanceUntilIdle()

        assertTrue(
            profileRepository.currentProfile?.dailyMantra == "Pendidikan adalah senjata paling ampuh untuk mengubah dunia."
        )
    }
}

class FakeUserProfileRepository : UserProfileRepository {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val currentProfile: UserProfile?
        get() = _profile.value
    
    override fun getProfile(): Flow<UserProfile?> = _profile.asStateFlow()
    
    override suspend fun saveProfile(profile: UserProfile) {
        _profile.value = profile
    }

    override suspend fun updateLocalProfile(name: String?, photoPath: String?, nim: String?, major: String?) {
        _profile.update { it?.copy(localName = name, localPhotoPath = photoPath, nim = nim ?: "", major = major ?: "") }
    }

    override suspend fun updateStreak(streak: Int, lastStudyDate: Long?) {
        _profile.update { it?.copy(currentStreak = streak, lastStudyDate = lastStudyDate) }
    }
    
    override suspend fun updateMantra(mantra: String) {
        _profile.update { it?.copy(dailyMantra = mantra) }
    }
}

class FakeMantraRepository : MantraRepository {
    private val mantras = listOf(
        "Pendidikan adalah senjata paling ampuh untuk mengubah dunia.",
        "Belajar hari ini, memimpin esok hari.",
        "Kesuksesan bukanlah akhir, kegagalan bukanlah fatal: keberanian untuk melanjutkanlah yang penting.",
        "Akar dari pendidikan memang pahit, namun buahnya sangat manis.",
        "Jangan pernah berhenti belajar, karena hidup tidak pernah berhenti mengajar."
    )

    override suspend fun getRandomMantra(excludeMantra: String?): String {
        return mantras.first { it != excludeMantra }
    }
}
