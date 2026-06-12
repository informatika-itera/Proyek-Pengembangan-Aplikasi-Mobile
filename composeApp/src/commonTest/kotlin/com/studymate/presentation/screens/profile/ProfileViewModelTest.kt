package com.studymate.presentation.screens.profile

import app.cash.turbine.test
import com.studymate.domain.model.ActivityDay
import com.studymate.domain.model.Reminder
import com.studymate.domain.model.UserProfile
import com.studymate.domain.repository.ActivityRepository
import com.studymate.domain.repository.AuthRepository
import com.studymate.domain.repository.ReminderRepository
import com.studymate.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var profileRepository: FakeUserProfileRepository
    private lateinit var activityRepository: FakeActivityRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var reminderRepository: FakeReminderRepository
    private lateinit var viewModel: ProfileViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        profileRepository = FakeUserProfileRepository()
        activityRepository = FakeActivityRepository()
        authRepository = FakeAuthRepository()
        reminderRepository = FakeReminderRepository()
        
        viewModel = ProfileViewModel(
            profileRepository,
            activityRepository,
            authRepository,
            reminderRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProfile should update uiState with user profile`() = runTest {
        val user = UserProfile(localName = "Test User", lifeGoals = "Study Hard")
        
        viewModel.uiState.test {
            // Initial state (might be Loading or empty)
            skipItems(1) 
            
            profileRepository.emitProfile(user)
            
            val state = awaitItem()
            assertEquals("Test User", state.user?.displayName)
            assertEquals("Study Hard", state.user?.lifeGoals)
        }
    }

    @Test
    fun `updateProfile should call repository`() = runTest {
        viewModel.updateProfile("New Name", "IT", "12345", "Be Awesome")
        advanceUntilIdle()
        
        val updatedUser = profileRepository.savedProfile
        assertNotNull(updatedUser)
        assertEquals("New Name", updatedUser.localName)
        assertEquals("Be Awesome", updatedUser.lifeGoals)
    }

    // Fakes for testing
    class FakeUserProfileRepository : UserProfileRepository {
        private val _profileFlow = MutableStateFlow<UserProfile?>(null)
        var savedProfile: UserProfile? = null

        fun emitProfile(user: UserProfile?) {
            _profileFlow.value = user
        }

        override fun getProfile(): Flow<UserProfile?> = _profileFlow

        override suspend fun saveProfile(profile: UserProfile) {
            savedProfile = profile
        }

        override suspend fun updateLocalProfile(name: String?, photoPath: String?, nim: String?, major: String?, lifeGoals: String?) {
            savedProfile = UserProfile(localName = name, localPhotoPath = photoPath, nim = nim ?: "", major = major ?: "", lifeGoals = lifeGoals ?: "")
        }

        override suspend fun updateStreak(streak: Int, lastStudyDate: Long?) {}
        override suspend fun updateMantra(mantra: String) {}
        override suspend fun clearProfile() {}
    }

    class FakeActivityRepository : ActivityRepository {
        override fun getActivityHeatmap(days: Int): Flow<List<ActivityDay>> = flowOf(emptyList())
        override suspend fun recordQuizCompletion() {}
        override suspend fun recordNoteCreation() {}
        override suspend fun getMonthlyQuizCount(): Int = 0
    }

    class FakeAuthRepository : AuthRepository {
        override val currentUser: StateFlow<UserProfile?> = MutableStateFlow(null)
        override suspend fun signIn(email: String, displayName: String?, photoUrl: String?): Result<UserProfile> = 
            Result.success(UserProfile(email = email, googleName = displayName, googlePhotoUrl = photoUrl))
        override suspend fun signOut() {}
    }

    class FakeReminderRepository : ReminderRepository {
        override fun getAllReminders(): Flow<List<Reminder>> = flowOf(emptyList())
        override suspend fun insertReminder(reminder: Reminder): Long = 0
        override suspend fun deleteReminder(id: Long) {}
    }
}
