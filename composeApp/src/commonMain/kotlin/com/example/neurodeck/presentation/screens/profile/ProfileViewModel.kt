package com.example.neurodeck.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.ReminderSettings
import com.example.neurodeck.domain.model.ThemeMode
import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.domain.reminder.ReminderScheduler
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.domain.repository.ReviewRecordRepository
import com.example.neurodeck.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * UI state untuk Profile Tab.
 *
 * @property profile        User profile (name, username, bio, avatar, memberSince).
 * @property themeMode      Current theme mode (untuk highlight di settings).
 * @property totalDecks     Achievement: jumlah deck yang dibuat user.
 * @property totalCards     Achievement: jumlah kartu total across all decks.
 * @property totalReviews   Achievement: jumlah review lifetime.
 * @property streakDays     Achievement: current streak.
 * @property isLoading      True saat initial load atau saat reset in-progress.
 * @property snackbarMessage One-shot message (consume by Screen, lalu clear).
 */
data class ProfileUiState(
    val profile: UserProfile = UserProfile(),
    val themeMode: ThemeMode = ThemeMode.System,
    val totalDecks: Int = 0,
    val totalCards: Int = 0,
    val totalReviews: Int = 0,
    val streakDays: Int = 0,
    val isLoading: Boolean = true,
    val snackbarMessage: String? = null,
    val reminderSettings: ReminderSettings = ReminderSettings(),
)

/**
 * ViewModel untuk Profile Tab — REACTIVE achievements (Sprint 3 upgrade).
 *
 * PERUBAHAN dari versi snapshot:
 *   Dulu: achievement stats di-fetch one-shot di init via refreshAchievements().
 *         Angka Decks/Cards/Reviews/Streak TIDAK update saat user review kartu.
 *   Sekarang: achievementsFlow derived dari observeAllDecks() (reactive trigger).
 *
 * State adalah combine dari 4 source reactive:
 *   - observeProfile()      → auto-update saat user edit profil
 *   - observeThemeMode()    → auto-update saat ganti theme
 *   - achievementsFlow      → auto-update saat decks/cards/review berubah
 *   - _snackbar             → one-shot message
 *
 * Kenapa observeAllDecks() jadi trigger achievements:
 *   - Query LEFT JOIN CardEntity → SQLDelight re-emit saat CardEntity berubah
 *   - User review kartu → card SM-2 state update → flow emit → achievements
 *     recompute (termasuk totalReviews & streak via suspend reads)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val deckRepository: DeckRepository,
    private val reviewRecordRepository: ReviewRecordRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val _snackbar = MutableStateFlow<String?>(null)

    /**
     * Achievement stats sebagai reactive Flow.
     * Derived dari observeAllDecks() — re-compute setiap kali cards berubah.
     * mapLatest: kalau emit baru datang sebelum compute selesai, batalkan yang lama.
     */
    private val achievementsFlow: Flow<Achievements> =
        deckRepository.observeAllDecks()
            .mapLatest { decks ->
                val now = Clock.System.now()
                Achievements(
                    totalDecks = decks.size,
                    totalCards = decks.sumOf { it.cardCount },
                    totalReviews = reviewRecordRepository.getTotalReviews(),
                    streakDays = reviewRecordRepository.getStreakDays(now),
                )
            }
            .catch {
                // Achievement non-kritis — fallback ke default kalau error,
                // jangan crash seluruh Profile screen.
                emit(Achievements())
            }

    val uiState: StateFlow<ProfileUiState> = combine(
        userPreferencesRepository.observeProfile(),
        userPreferencesRepository.observeThemeMode(),
        achievementsFlow,
        _snackbar,
        userPreferencesRepository.observeReminderSettings(),
    ) { profile, themeMode, achievements, snackbarMsg, reminderSettings ->
        ProfileUiState(
            profile = profile,
            themeMode = themeMode,
            totalDecks = achievements.totalDecks,
            totalCards = achievements.totalCards,
            totalReviews = achievements.totalReviews,
            streakDays = achievements.streakDays,
            isLoading = false,
            snackbarMessage = snackbarMsg,
            reminderSettings = reminderSettings,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true),
    )

    // ════════════════════════════════════════════════════════════════════════
    // SETTINGS ACTIONS
    // ════════════════════════════════════════════════════════════════════════

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setThemeMode(mode)
                val label = when (mode) {
                    ThemeMode.Light -> "Light"
                    ThemeMode.Dark -> "Dark"
                    ThemeMode.System -> "System"
                }
                _snackbar.value = "✅ Mode tampilan diubah ke $label"
            } catch (e: Exception) {
                _snackbar.value = "Gagal mengganti tema: ${e.message ?: "unknown"}"
            }
        }
    }

    /**
     * Atur reminder belajar harian.
     *
     * Persist setting ke DataStore, lalu jadwalkan/batalkan notifikasi via
     * [ReminderScheduler]. Saat enabled → schedule; saat disabled → cancel.
     */
    fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setReminderSettings(
                    ReminderSettings(enabled = enabled, hour = hour, minute = minute),
                )
                if (enabled) {
                    reminderScheduler.schedule(hour, minute)
                    val time = hour.toString().padStart(2, '0') + ":" +
                            minute.toString().padStart(2, '0')
                    _snackbar.value = "🔔 Pengingat belajar diatur jam $time"
                } else {
                    reminderScheduler.cancel()
                    _snackbar.value = "Pengingat belajar dimatikan"
                }
            } catch (e: Exception) {
                _snackbar.value = "Gagal atur pengingat: ${e.message ?: "unknown"}"
            }
        }
    }

    /**
     * Reset preferences (profile + theme). Achievement flow auto-update sendiri
     * kalau ada perubahan data (reactive), jadi tidak perlu manual refresh.
     */
    fun resetAllData() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.resetPreferences()
                _snackbar.value = "Preferensi berhasil di-reset"
            } catch (e: Exception) {
                _snackbar.value = "Gagal reset: ${e.message ?: "unknown"}"
            }
        }
    }

    /** Clear snackbar message after Screen consumed it. */
    fun consumeSnackbar() {
        _snackbar.value = null
    }

    // ════════════════════════════════════════════════════════════════════════
    // INTERNAL DATA HOLDER
    // ════════════════════════════════════════════════════════════════════════

    private data class Achievements(
        val totalDecks: Int = 0,
        val totalCards: Int = 0,
        val totalReviews: Int = 0,
        val streakDays: Int = 0,
    )
}