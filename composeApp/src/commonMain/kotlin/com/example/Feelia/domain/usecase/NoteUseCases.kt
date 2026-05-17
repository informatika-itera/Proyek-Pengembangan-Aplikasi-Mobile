package com.example.Feelia.domain.usecase

import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import com.example.Feelia.domain.repository.AIRepository
import com.example.Feelia.domain.repository.NoteRepository
import com.example.Feelia.domain.repository.WritingStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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