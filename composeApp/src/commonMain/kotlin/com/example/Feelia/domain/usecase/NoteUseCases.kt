package com.example.Feelia.domain.usecase

import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import com.example.Feelia.domain.repository.AIRepository
import com.example.Feelia.domain.repository.NoteRepository
import com.example.Feelia.domain.repository.WritingStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.Feelia.domain.model.EmotionResult
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

// validasi save note, logic sorting, search/filter berdasarkan emotion dan update business rules journaling
class GetAllNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC): Flow<List<Note>> {
        return repository.getAllNotes().map { notes ->
            val (pinned, unpinned) = notes.partition { it.isPinned }
            sortNotes(pinned, sortBy) + sortNotes(unpinned, sortBy)
        }
    }

    private fun sortNotes(notes: List<Note>, sortBy: NoteSortBy): List<Note> = when (sortBy) {
        NoteSortBy.CREATED_ASC -> notes.sortedBy { it.createdAt }
        NoteSortBy.CREATED_DESC -> notes.sortedByDescending { it.createdAt }
        NoteSortBy.UPDATED_ASC -> notes.sortedBy { it.updatedAt }
        NoteSortBy.UPDATED_DESC -> notes.sortedByDescending { it.updatedAt }
    }
}

enum class NoteSortBy(val displayName: String) {
    CREATED_ASC("Terlama"),
    CREATED_DESC("Terbaru"),
    UPDATED_ASC("Diupdate (Lama)"),
    UPDATED_DESC("Diupdate (Baru)")
}

class SearchNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(query: String, emotion: Emotion? = null): Flow<List<Note>> {
        return if (query.isBlank() && emotion == null) {
            repository.getAllNotes()
        } else if (query.isBlank()) {
            repository.getNotesByEmotion(emotion!!)
        } else {
            repository.searchNotes(query).map { notes ->
                if (emotion != null) notes.filter { it.emotion == emotion } else notes
            }
        }
    }
}

class SaveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note): Result<Long> {
        return try {
            if (note.content.isBlank()) {
                return Result.failure(IllegalArgumentException("Jurnal tidak boleh kosong"))
            }
            if (note.content.trim().length < 10) {
                return Result.failure(IllegalArgumentException("Ceritakan lebih banyak tentang harimu (min. 10 karakter)"))
            }
            val id = if (note.id == 0L) repository.insertNote(note)
            else { repository.updateNote(note); note.id }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteNote(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SummarizeNoteUseCase(private val aiRepository: AIRepository) {
    suspend operator fun invoke(content: String): Result<String> {
        if (content.length < 50)
            return Result.failure(IllegalArgumentException("Konten terlalu pendek"))
        return aiRepository.summarize(content)
    }
}

class ImproveWritingUseCase(private val aiRepository: AIRepository) {
    suspend operator fun invoke(content: String, style: WritingStyle = WritingStyle.NEUTRAL): Result<String> {
        if (content.isBlank())
            return Result.failure(IllegalArgumentException("Konten tidak boleh kosong"))
        return aiRepository.improveWriting(content, style)
    }
}

class GenerateIdeasUseCase(private val aiRepository: AIRepository) {
    suspend operator fun invoke(topic: String): Result<List<String>> {
        if (topic.isBlank())
            return Result.failure(IllegalArgumentException("Topik tidak boleh kosong"))
        return aiRepository.generateIdeas(topic)
    }
}

class DetectEmotionUseCase(private val aiRepository: AIRepository) {
    suspend operator fun invoke(content: String): Result<Emotion> {
        if (content.trim().length < 5)
            return Result.failure(IllegalArgumentException("Teks terlalu pendek"))
        return aiRepository.detectEmotion(content).map { raw ->
            Emotion.fromString(raw)
        }
    }
}

class GetEmotionInsightUseCase(private val aiRepository: AIRepository) {
    suspend operator fun invoke(content: String, emotion: Emotion): Result<String> {
        return aiRepository.getEmotionInsight(content, emotion.displayName)
    }
}

class DetectEmotionWithInsightUseCase(private val aiRepository: AIRepository) {
    suspend operator fun invoke(content: String): Result<EmotionResult> {
        if (content.trim().length < 5)
            return Result.failure(IllegalArgumentException("Teks terlalu pendek"))
        return aiRepository.detectEmotionWithInsight(content)
    }
}
// ==================== ANALYTICS USE CASES ====================

data class EmotionStat(
    val emotion: Emotion,
    val count: Int,
    val percentage: Float
)

data class MoodTrendData(
    val date: String,
    val emotion: Emotion,
    val count: Int
)

data class WeeklyInsight(
    val dominantEmotion: Emotion,
    val totalJournals: Int,
    val positivePercentage: Float,
    val negativePercentage: Float,
    val emotionStats: List<EmotionStat>,
    val moodTrend: List<MoodTrendData>,
    val streakDays: Int
)

data class WordFrequency(
    val word: String,
    val count: Int,
    val associatedEmotion: Emotion
)

class GetWeeklyInsightUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<WeeklyInsight> {
        return repository.getAllNotes().map { notes ->
            val now = Clock.System.now()
            val sevenDaysAgo = now.minus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            val recentNotes = notes.filter { it.createdAt >= sevenDaysAgo }

            val emotionCounts = Emotion.entries.associateWith { emotion ->
                recentNotes.count { it.emotion == emotion }
            }

            val total = recentNotes.size.coerceAtLeast(1)

            val emotionStats = emotionCounts.map { (emotion, count) ->
                EmotionStat(
                    emotion = emotion,
                    count = count,
                    percentage = (count.toFloat() / total * 100)
                )
            }.sortedByDescending { it.count }

            val dominantEmotion = emotionStats.firstOrNull()?.emotion ?: Emotion.NEUTRAL

            val positiveEmotions = setOf(Emotion.HAPPY)
            val negativeEmotions = setOf(Emotion.SAD, Emotion.ANXIOUS, Emotion.ANGRY)
            val positiveCount = recentNotes.count { it.emotion in positiveEmotions }
            val negativeCount = recentNotes.count { it.emotion in negativeEmotions }

            val moodTrend = buildMoodTrend(recentNotes)
            val streak = calculateStreak(notes)

            WeeklyInsight(
                dominantEmotion = dominantEmotion,
                totalJournals = recentNotes.size,
                positivePercentage = positiveCount.toFloat() / total * 100,
                negativePercentage = negativeCount.toFloat() / total * 100,
                emotionStats = emotionStats,
                moodTrend = moodTrend,
                streakDays = streak
            )
        }
    }

    private fun buildMoodTrend(notes: List<Note>): List<MoodTrendData> {
        val grouped = notes.groupBy { note ->
            val dt = note.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            "${dt.dayOfMonth}/${dt.monthNumber}"
        }
        return grouped.map { (date, dayNotes) ->
            val dominant = dayNotes.groupBy { it.emotion }
                .maxByOrNull { it.value.size }?.key ?: Emotion.NEUTRAL
            MoodTrendData(date = date, emotion = dominant, count = dayNotes.size)
        }.sortedBy { it.date }
    }

    private fun calculateStreak(notes: List<Note>): Int {
        if (notes.isEmpty()) return 0
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        var streak = 0
        var checkDate = today
        val noteDates = notes.map {
            it.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }.toSet()
        while (noteDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minus(1, DateTimeUnit.DAY)
        }
        return streak
    }
}

class GetFrequentWordsUseCase(private val repository: NoteRepository) {
    private val stopWords = setOf(
        "yang", "dan", "di", "ke", "dari", "dengan", "ini", "itu",
        "untuk", "pada", "adalah", "juga", "saya", "aku", "kamu",
        "dia", "kami", "kita", "mereka", "ada", "tidak", "sudah",
        "akan", "bisa", "karena", "tapi", "atau", "jadi", "lagi",
        "hari", "nya", "si", "ya", "aja", "deh", "sih", "nih",
        "banget", "sangat", "lebih", "banyak", "kalau", "kalau",
        "seperti", "buat", "sama", "mau", "udah", "lalu", "terus"
    )

    operator fun invoke(emotion: Emotion? = null): Flow<List<WordFrequency>> {
        return repository.getAllNotes().map { notes ->
            val filtered = if (emotion != null) notes.filter { it.emotion == emotion } else notes
            val wordEmotionMap = mutableMapOf<String, MutableMap<Emotion, Int>>()
            filtered.forEach { note ->
                note.content.lowercase()
                    .replace(Regex("[^a-zA-ZäöüÄÖÜa-zA-Z0-9\\s]"), " ")
                    .split("\\s+".toRegex())
                    .filter { it.length > 3 && it !in stopWords }
                    .forEach { word ->
                        wordEmotionMap.getOrPut(word) { mutableMapOf() }
                            .merge(note.emotion, 1, Int::plus)
                    }
            }
            wordEmotionMap.map { (word, emotionCounts) ->
                val dominantEmotion = emotionCounts.maxByOrNull { it.value }?.key ?: Emotion.NEUTRAL
                val totalCount = emotionCounts.values.sum()
                WordFrequency(word = word, count = totalCount, associatedEmotion = dominantEmotion)
            }
                .filter { it.count >= 2 }
                .sortedByDescending { it.count }
                .take(20)
        }
    }
}

class GetAIWeeklyInsightUseCase(
    private val repository: NoteRepository,
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(): Result<String> {
        val notes = repository.getAllNotes()
            .map { list ->
                val now = Clock.System.now()
                val sevenDaysAgo = now.minus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                list.filter { it.createdAt >= sevenDaysAgo }
            }
            .first()

        if (notes.isEmpty()) {
            return Result.failure(IllegalStateException("Belum ada jurnal minggu ini"))
        }

        val summary = notes.joinToString("\n") { note ->
            "- [${note.emotion.displayName}] ${note.content.take(100)}"
        }

        val prompt = """
            Berikut adalah ringkasan jurnal emosi pengguna selama 7 hari terakhir:
            
            $summary
            
            Berikan insight personal yang hangat (2-3 kalimat) tentang pola emosi pengguna.
            Sertakan satu saran praktis.
            Gunakan kata 'kamu', bahasa Indonesia yang santai dan supportif.
        """.trimIndent()

        return aiRepository.chat(prompt)
    }
}
// Tambahkan ini di paling bawah file untuk mensimulasikan fungsi .merge() milik Java
fun <K, V> MutableMap<K, V>.merge(key: K, value: V, remappingFunction: (V, V) -> V): V {
    val oldValue = this[key]
    val newValue = if (oldValue == null) value else remappingFunction(oldValue, value)
    if (newValue == null) {
        this.remove(key)
    } else {
        this[key] = newValue
    }
    return newValue
}