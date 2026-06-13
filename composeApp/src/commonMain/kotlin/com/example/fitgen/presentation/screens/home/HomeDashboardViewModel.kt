package com.example.fitgen.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.core.network.NetworkResult
import com.example.fitgen.data.local.datastore.UserPreferences
import com.example.fitgen.data.remote.api.ExerciseApiService
import com.example.fitgen.data.remote.dto.ExerciseDto
import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.usecase.GetAllWorkoutsUseCase
import com.example.fitgen.domain.usecase.GetDailyCaloriesUseCase
import com.example.fitgen.domain.usecase.UpdateLoginStreakUseCase
import com.example.fitgen.domain.usecase.AddWaterGlassUseCase
import com.example.fitgen.domain.usecase.GetDailyWaterGlassesUseCase
import com.example.fitgen.domain.usecase.GetLoginStreakUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeDashboardUiState(
    val isLoading: Boolean              = true,
    val userName: String                = "",
    val profileImageBytes: ByteArray?   = null,
    val totalCaloriesToday: Int         = 0,
    val calorieTarget: Int              = 2000,
    val activeStreakDays: Int           = 0,
    val waterGlassesToday: Int          = 0,
    val lastWorkout: WorkoutLog?        = null,
    val challenges: List<ExerciseDto>   = emptyList(),
    val isChallengesLoading: Boolean    = true,
    val errorMessage: String?           = null,
    // Classic Workout per kategori
    val selectedClassicCategory: String? = "Push",
    val classicWorkouts: List<ExerciseDto> = emptyList(),
    val isClassicLoading: Boolean       = true
) {
    val calorieProgress: Float
        get() = if (calorieTarget > 0) totalCaloriesToday.toFloat() / calorieTarget else 0f
}

class HomeDashboardViewModel(
    private val getDailyCaloriesUseCase: GetDailyCaloriesUseCase,
    private val getAllWorkoutsUseCase: GetAllWorkoutsUseCase,
    private val updateLoginStreakUseCase: UpdateLoginStreakUseCase,
    private val getDailyWaterGlassesUseCase: GetDailyWaterGlassesUseCase,
    private val getLoginStreakUseCase: GetLoginStreakUseCase,
    private val addWaterGlassUseCase: AddWaterGlassUseCase,
    private val userPreferences: UserPreferences,
    private val exerciseApiService: ExerciseApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeDashboardUiState())
    val uiState: StateFlow<HomeDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
        loadUserName()
        loadChallenges()
        loadDefaultClassicCategory() // auto-open Push tanpa trigger toggle
    }

    private fun loadDashboard() {
        // Trigger update streak saat dashboard dimuat
        viewModelScope.launch {
            updateLoginStreakUseCase()
        }

        combine(
            getDailyCaloriesUseCase(),
            getAllWorkoutsUseCase(),
            getDailyWaterGlassesUseCase(),
            getLoginStreakUseCase()
        ) { meals, workouts, water, streak ->
            val totalKalori = getDailyCaloriesUseCase.calculateTotal(meals)
            val lastWorkout = workouts.firstOrNull()

            _uiState.update {
                it.copy(
                    isLoading          = false,
                    totalCaloriesToday = totalKalori,
                    lastWorkout        = lastWorkout,
                    activeStreakDays   = streak,
                    waterGlassesToday  = water,
                    errorMessage       = null
                )
            }
        }
        .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
        .launchIn(viewModelScope)
    }

    @OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
    private fun loadUserName() {
        viewModelScope.launch {
            userPreferences.userProfile.collect { profile ->
                _uiState.update { it.copy(userName = profile.name) }
            }
        }
        viewModelScope.launch {
            userPreferences.userProfileImageBase64.collect { base64 ->
                if (base64 != null) {
                    try {
                        val bytes = kotlin.io.encoding.Base64.decode(base64)
                        _uiState.update { it.copy(profileImageBytes = bytes) }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(profileImageBytes = null) }
                    }
                } else {
                    _uiState.update { it.copy(profileImageBytes = null) }
                }
            }
        }
    }

    private fun loadChallenges() {
        _uiState.update { 
            it.copy(
                isChallengesLoading = false,
                challenges = listOf(
                    ExerciseDto(
                        id = "fullbody",
                        name = "30-Day Full Body Transformation",
                        gifUrl = "challenge_full_body",
                        bodyPart = "Full Body"
                    ),
                    ExerciseDto(
                        id = "core",
                        name = "14-Day Core Crusher",
                        gifUrl = "challenge_core",
                        bodyPart = "Core"
                    ),
                    ExerciseDto(
                        id = "upperbody",
                        name = "21-Day Upper Body Power",
                        gifUrl = "challenge_upper_body",
                        bodyPart = "Upper Body"
                    )
                )
            ) 
        }
    }

    /**
     * Load kategori default (Push) saat pertama kali dibuka.
     * Tidak menggunakan selectClassicCategory agar tidak terkena logika toggle-off.
     */
    private fun loadDefaultClassicCategory() {
        val defaultCategory = "Push"
        _uiState.update { it.copy(selectedClassicCategory = defaultCategory, isClassicLoading = true, classicWorkouts = emptyList()) }
        viewModelScope.launch {
            when (val result = exerciseApiService.getExercisesByCategory(defaultCategory)) {
                is com.example.fitgen.core.network.NetworkResult.Success -> {
                    _uiState.update { it.copy(classicWorkouts = result.data, isClassicLoading = false) }
                }
                else -> {
                    _uiState.update { it.copy(classicWorkouts = emptyList(), isClassicLoading = false) }
                }
            }
        }
    }

    fun addWater() {
        viewModelScope.launch {
            addWaterGlassUseCase()
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    /**
     * Dipanggil saat user klik chip kategori classic workout.
     * Jika kategori yang sama diklik lagi, tutup (toggle off).
     */
    fun selectClassicCategory(categoryLabel: String) {
        val current = _uiState.value.selectedClassicCategory
        if (current == categoryLabel) {
            // Toggle off — tutup list
            _uiState.update { it.copy(selectedClassicCategory = null, classicWorkouts = emptyList()) }
            return
        }
        _uiState.update { it.copy(selectedClassicCategory = categoryLabel, isClassicLoading = true, classicWorkouts = emptyList()) }
        viewModelScope.launch {
            when (val result = exerciseApiService.getExercisesByCategory(categoryLabel)) {
                is com.example.fitgen.core.network.NetworkResult.Success -> {
                    _uiState.update { it.copy(classicWorkouts = result.data, isClassicLoading = false) }
                }
                else -> {
                    _uiState.update { it.copy(classicWorkouts = emptyList(), isClassicLoading = false) }
                }
            }
        }
    }
}