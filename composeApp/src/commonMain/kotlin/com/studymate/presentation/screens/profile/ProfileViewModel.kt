package com.studymate.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.domain.model.ActivityDay
import com.studymate.domain.model.UserProfile
import com.studymate.domain.model.AchievementTier
import com.studymate.domain.model.Reminder
import com.studymate.domain.repository.ActivityRepository
import com.studymate.domain.repository.UserProfileRepository
import com.studymate.domain.repository.AuthRepository
import com.studymate.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class ProfileState(
    val user: UserProfile? = null,
    val heatmap: List<ActivityDay> = emptyList(),
    val monthlyQuizCount: Int = 0,
    val isLoading: Boolean = false,
    val closestReminder: Reminder? = null,
) {
    val achievementTier: AchievementTier
        get() = when {
            monthlyQuizCount >= 15 -> AchievementTier.GOLD
            monthlyQuizCount >= 10 -> AchievementTier.SILVER
            monthlyQuizCount >= 5 -> AchievementTier.BRONZE
            else -> AchievementTier.NONE
        }
}

class ProfileViewModel(
    private val profileRepository: UserProfileRepository,
    private val activityRepository: ActivityRepository,
    private val authRepository: AuthRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    // Observe reminders via a StateFlow to ensure updates are received reliably
    private val remindersFlow: StateFlow<List<Reminder>> = reminderRepository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadProfile()
        loadHeatmap()
        loadAchievements()
        observeReminders()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.getProfile().collect { user ->
                _uiState.update { it.copy(user = user, isLoading = false) }
            }
        }
    }

    private fun loadHeatmap() {
        activityRepository.getActivityHeatmap(30)
            .onEach { heatmap ->
                _uiState.update { it.copy(heatmap = heatmap) }
            }
            .launchIn(viewModelScope)
    }
    
    private fun loadAchievements() {
        viewModelScope.launch {
            val count = activityRepository.getMonthlyQuizCount()
            _uiState.update { it.copy(monthlyQuizCount = count) }
        }
    }

    private fun observeReminders() {
        remindersFlow
            .onEach { reminders ->
                val now = Clock.System.now().toEpochMilliseconds()
                val closestReminder = reminders
                    .filter { (!it.isCompleted) && (it.dueDate > now) }
                    .minByOrNull { it.dueDate }
                _uiState.update { it.copy(closestReminder = closestReminder) }
            }
            .launchIn(viewModelScope)
    }

    fun updateProfile(name: String, major: String, nim: String) {
        viewModelScope.launch {
            profileRepository.updateLocalProfile(name, null, nim, major)
        }
    }

    fun signInWithGoogle(email: String, displayName: String?, photoUrl: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.signIn(email, displayName, photoUrl)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
