package com.studymate.presentation

import app.cash.turbine.test
import com.studymate.data.repository.FakeNoteRepository
import com.studymate.domain.model.Note
import com.studymate.domain.model.UserProfile
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
    
    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var profileRepository: FakeUserProfileRepository
    private lateinit var viewModel: HomeViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        noteRepository = FakeNoteRepository()
        profileRepository = FakeUserProfileRepository()
        
        viewModel = HomeViewModel(
            noteRepository = noteRepository,
            profileRepository = profileRepository
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
}

class FakeUserProfileRepository : UserProfileRepository {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    
    override fun getProfile(): Flow<UserProfile?> = _profile.asStateFlow()
    
    override suspend fun saveProfile(profile: UserProfile) {
        _profile.value = profile
    }
    
    override suspend fun updateMantra(mantra: String) {
        _profile.update { it?.copy(dailyMantra = mantra) }
    }
}
