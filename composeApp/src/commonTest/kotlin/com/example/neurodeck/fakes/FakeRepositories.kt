package com.example.neurodeck.fakes

import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.domain.model.ChatMessage
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.model.MessageRole
import com.example.neurodeck.domain.model.ReviewRating
import com.example.neurodeck.domain.model.ReminderSettings
import com.example.neurodeck.domain.model.ThemeMode
import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.domain.repository.AIRepository
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.ChatRepository
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.domain.repository.ReviewRecordRepository
import com.example.neurodeck.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Kumpulan fake repository untuk unit test ViewModel.
 *
 * Filosofi: fake (bukan mock) — punya implementasi in-memory beneran
 * supaya behavior-nya realistic (Flow re-emit saat data berubah, dll).
 * Tiap fake expose:
 *   - State backing (MutableStateFlow / var) yang bisa di-seed test.
 *   - Flag `throwOn*` / `*Error` untuk simulasi error path.
 *   - Recording field (mis. `lastCreatedTitle`, `deletedIds`) untuk verifikasi
 *     bahwa ViewModel memanggil repository dengan argumen yang benar.
 *
 * Catatan: fake TIDAK pakai withContext(Dispatchers.IO) seperti impl asli —
 * semua suspend langsung return supaya deterministic di bawah test dispatcher.
 */

// ════════════════════════════════════════════════════════════════════════════
// DECK
// ════════════════════════════════════════════════════════════════════════════

class FakeDeckRepository : DeckRepository {

    val decksFlow = MutableStateFlow<List<Deck>>(emptyList())

    /** Set non-null untuk membuat observeAllDecks() melempar error (uji Error state). */
    var observeError: Throwable? = null

    var throwOnCreate = false
    var throwOnUpdate = false
    var throwOnDelete = false

    private var nextId = 1L

    // Recording
    var lastCreatedTitle: String? = null
    var lastCreatedDescription: String? = null
    var lastUpdatedDeck: Deck? = null
    val deletedIds = mutableListOf<Long>()
    var createCallCount = 0

    /** Helper test: seed daftar deck langsung. */
    fun setDecks(decks: List<Deck>) {
        decksFlow.value = decks
    }

    override fun observeAllDecks(): Flow<List<Deck>> =
        observeError?.let { err -> flow<List<Deck>> { throw err } } ?: decksFlow

    override fun observeDeckById(id: Long): Flow<Deck?> =
        decksFlow.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun createDeck(title: String, description: String): Long {
        createCallCount++
        if (throwOnCreate) throw RuntimeException("createDeck gagal")
        lastCreatedTitle = title
        lastCreatedDescription = description
        val id = nextId++
        decksFlow.value = decksFlow.value + Deck(id = id, title = title, description = description)
        return id
    }

    override suspend fun updateDeck(deck: Deck) {
        if (throwOnUpdate) throw RuntimeException("updateDeck gagal")
        lastUpdatedDeck = deck
        decksFlow.value = decksFlow.value.map { if (it.id == deck.id) deck else it }
    }

    override suspend fun deleteDeck(id: Long) {
        if (throwOnDelete) throw RuntimeException("deleteDeck gagal")
        deletedIds += id
        decksFlow.value = decksFlow.value.filterNot { it.id == id }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CARD
// ════════════════════════════════════════════════════════════════════════════

class FakeCardRepository : CardRepository {

    val cardsFlow = MutableStateFlow<List<Card>>(emptyList())

    /** Kartu yang dianggap "due" — dikembalikan observeDueCards (difilter by deckId). */
    var dueCards: List<Card> = emptyList()

    var cardByIdResult: Card? = null
    var allDueCount: Long = 0L

    var observeDueError: Throwable? = null
    var throwOnGetById = false
    var throwOnCreate = false
    var throwOnUpdate = false
    var throwOnDelete = false
    var throwOnReview = false

    private var nextId = 1L

    // Recording
    var lastCreatedFront: String? = null
    var lastCreatedBack: String? = null
    var lastCreatedDeckId: Long? = null
    var lastBulkCreate: List<Pair<String, String>>? = null
    var lastUpdatedContent: Triple<Long, String, String>? = null
    val deletedIds = mutableListOf<Long>()
    val reviewedCards = mutableListOf<Pair<Long, ReviewRating>>()

    override fun observeCardsByDeck(deckId: Long): Flow<List<Card>> =
        cardsFlow.map { list -> list.filter { it.deckId == deckId } }

    override fun observeDueCards(deckId: Long, now: Instant): Flow<List<Card>> =
        observeDueError?.let { err -> flow<List<Card>> { throw err } }
            ?: flowOf(dueCards.filter { it.deckId == deckId })

    override suspend fun getCardById(cardId: Long): Card? {
        if (throwOnGetById) throw RuntimeException("getCardById gagal")
        return cardByIdResult
    }

    override suspend fun countAllDueCards(now: Instant): Long = allDueCount

    override suspend fun createCard(deckId: Long, front: String, back: String): Long {
        if (throwOnCreate) throw RuntimeException("createCard gagal")
        lastCreatedDeckId = deckId
        lastCreatedFront = front
        lastCreatedBack = back
        val id = nextId++
        cardsFlow.value = cardsFlow.value + Card(id = id, deckId = deckId, front = front, back = back)
        return id
    }

    override suspend fun createCards(
        deckId: Long,
        cards: List<Pair<String, String>>,
    ): List<Long> {
        if (throwOnCreate) throw RuntimeException("createCards gagal")
        lastBulkCreate = cards
        lastCreatedDeckId = deckId
        val ids = mutableListOf<Long>()
        val newCards = cards.map { (front, back) ->
            val id = nextId++
            ids += id
            Card(id = id, deckId = deckId, front = front, back = back)
        }
        cardsFlow.value = cardsFlow.value + newCards
        return ids
    }

    override suspend fun updateCardContent(id: Long, front: String, back: String) {
        if (throwOnUpdate) throw RuntimeException("updateCardContent gagal")
        lastUpdatedContent = Triple(id, front, back)
    }

    override suspend fun deleteCard(id: Long) {
        if (throwOnDelete) throw RuntimeException("deleteCard gagal")
        deletedIds += id
    }

    override suspend fun reviewCard(cardId: Long, rating: ReviewRating, now: Instant) {
        if (throwOnReview) throw RuntimeException("reviewCard gagal")
        reviewedCards += cardId to rating
    }
}

// ════════════════════════════════════════════════════════════════════════════
// REVIEW RECORD
// ════════════════════════════════════════════════════════════════════════════

class FakeReviewRecordRepository : ReviewRecordRepository {

    var reviewedToday = 0
    var streakDays = 0
    var totalReviews = 0
    var reviewsInRange = 0
    var accuracyInRange = 0.0
    var dailyActivity: Map<Int, Int> = emptyMap()

    var throwOnStreak = false

    override suspend fun getReviewedToday(now: Instant): Int = reviewedToday

    override suspend fun getStreakDays(now: Instant): Int {
        if (throwOnStreak) throw RuntimeException("getStreakDays gagal")
        return streakDays
    }

    override suspend fun getTotalReviews(): Int = totalReviews

    override suspend fun countReviewsInRange(fromInclusive: Instant, toExclusive: Instant): Int =
        reviewsInRange

    override suspend fun getAccuracyInRange(fromInclusive: Instant, toExclusive: Instant): Double =
        accuracyInRange

    override suspend fun getDailyActivity(daysBack: Int, now: Instant): Map<Int, Int> =
        dailyActivity
}

// ════════════════════════════════════════════════════════════════════════════
// USER PREFERENCES
// ════════════════════════════════════════════════════════════════════════════

class FakeUserPreferencesRepository : UserPreferencesRepository {

    val profileFlow = MutableStateFlow(UserProfile())
    val themeModeFlow = MutableStateFlow(ThemeMode.System)
    val reminderFlow = MutableStateFlow(ReminderSettings())

    var throwOnGetProfile = false
    var throwOnSave = false
    var throwOnSetTheme = false
    var throwOnReset = false

    // Recording
    var savedProfile: UserProfile? = null
    var resetCalled = false
    var lastThemeSet: ThemeMode? = null
    var lastReminderSet: ReminderSettings? = null

    override fun observeProfile(): Flow<UserProfile> = profileFlow

    override suspend fun getProfile(): UserProfile {
        if (throwOnGetProfile) throw RuntimeException("getProfile gagal")
        return profileFlow.value
    }

    override suspend fun saveProfile(profile: UserProfile) {
        if (throwOnSave) throw RuntimeException("saveProfile gagal")
        savedProfile = profile
        profileFlow.value = profile
    }

    override fun observeThemeMode(): Flow<ThemeMode> = themeModeFlow

    override suspend fun setThemeMode(mode: ThemeMode) {
        if (throwOnSetTheme) throw RuntimeException("setThemeMode gagal")
        lastThemeSet = mode
        themeModeFlow.value = mode
    }

    override fun observeReminderSettings(): Flow<ReminderSettings> = reminderFlow

    override suspend fun setReminderSettings(settings: ReminderSettings) {
        lastReminderSet = settings
        reminderFlow.value = settings
    }

    override suspend fun resetPreferences() {
        if (throwOnReset) throw RuntimeException("resetPreferences gagal")
        resetCalled = true
        profileFlow.value = UserProfile()
        themeModeFlow.value = ThemeMode.System
    }
}

// ════════════════════════════════════════════════════════════════════════════
// AI
// ════════════════════════════════════════════════════════════════════════════

class FakeAIRepository : AIRepository {

    var flashcardsResult: List<Pair<String, String>> = emptyList()
    var chatReply: String = "Ini jawaban dari AI Tutor."

    var throwOnGenerate = false
    var throwOnChat = false

    // Recording
    var lastMaterial: String? = null
    var lastHistory: List<Pair<String, String>>? = null

    override suspend fun generateFlashcards(material: String): List<Pair<String, String>> {
        lastMaterial = material
        if (throwOnGenerate) throw RuntimeException("Gagal generate flashcards")
        return flashcardsResult
    }

    override suspend fun chatWithHistory(history: List<Pair<String, String>>): String {
        lastHistory = history
        if (throwOnChat) throw RuntimeException("Gagal chat dengan AI")
        return chatReply
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CHAT
// ════════════════════════════════════════════════════════════════════════════

class FakeChatRepository : ChatRepository {

    val messagesFlow = MutableStateFlow<List<ChatMessage>>(emptyList())

    /** Hasil yang dikembalikan sendMessage(). Default success. */
    var sendResult: Result<Unit> = Result.success(Unit)

    var throwOnClear = false

    // Recording
    val sentMessages = mutableListOf<String>()
    var clearCalled = false

    override fun observeMessages(): Flow<List<ChatMessage>> = messagesFlow

    override suspend fun sendMessage(userMessage: String): Result<Unit> {
        sentMessages += userMessage
        // Simulasikan: pada sukses, user message + balasan masuk ke history.
        if (sendResult.isSuccess) {
            val now = Clock.System.now()
            messagesFlow.value = messagesFlow.value +
                    ChatMessage(role = MessageRole.User, content = userMessage, timestamp = now) +
                    ChatMessage(role = MessageRole.Assistant, content = "balasan", timestamp = now)
        }
        return sendResult
    }

    override suspend fun clearHistory() {
        if (throwOnClear) throw RuntimeException("clearHistory gagal")
        clearCalled = true
        messagesFlow.value = emptyList()
    }
}