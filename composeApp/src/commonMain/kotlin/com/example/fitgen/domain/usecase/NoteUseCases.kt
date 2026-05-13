package com.example.fitgen.domain.usecase

import com.example.fitgen.domain.model.Note
import com.example.fitgen.domain.model.NoteCategory
import com.example.fitgen.domain.repository.AIRepository
import com.example.fitgen.domain.repository.NoteRepository
import com.example.fitgen.domain.repository.WritingStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC): Flow<List<Note>> {
        return repository.getAllNotes().map { notes ->
            val (pinned, unpinned) = notes.partition { it.isPinned }
            val sortedPinned = sortNotes(pinned, sortBy)
            val sortedUnpinned = sortNotes(unpinned, sortBy)
            sortedPinned + sortedUnpinned
        }
    }
    
    private fun sortNotes(notes: List<Note>, sortBy: NoteSortBy): List<Note> {
        return when (sortBy) {
            NoteSortBy.TITLE_ASC -> notes.sortedBy { it.title.lowercase() }
            NoteSortBy.TITLE_DESC -> notes.sortedByDescending { it.title.lowercase() }
            NoteSortBy.CREATED_ASC -> notes.sortedBy { it.createdAt }
            NoteSortBy.CREATED_DESC -> notes.sortedByDescending { it.createdAt }
            NoteSortBy.UPDATED_ASC -> notes.sortedBy { it.updatedAt }
            NoteSortBy.UPDATED_DESC -> notes.sortedByDescending { it.updatedAt }
        }
    }
}

enum class NoteSortBy(val displayName: String) {
    TITLE_ASC("Judul (A-Z)"),
    TITLE_DESC("Judul (Z-A)"),
    CREATED_ASC("Dibuat (Lama)"),
    CREATED_DESC("Dibuat (Baru)"),
    UPDATED_ASC("Diupdate (Lama)"),
    UPDATED_DESC("Diupdate (Baru)")
}

class SearchNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(query: String, category: NoteCategory? = null): Flow<List<Note>> {
        return if (query.isBlank() && category == null) {
            repository.getAllNotes()
        } else if (query.isBlank()) {
            repository.getNotesByCategory(category!!)
        } else {
            repository.searchNotes(query).map { notes ->
                if (category != null) {
                    notes.filter { it.category == category }
                } else {
                    notes
                }
            }
        }
    }
}

class SaveNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Result<Long> {
        return try {
            if (note.title.isBlank() && note.content.isBlank()) {
                return Result.failure(IllegalArgumentException("Note tidak boleh kosong"))
            }
            
            val id = if (note.id == 0L) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note)
                note.id
            }
            
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteNote(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SummarizeNoteUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(content: String): Result<String> {
        if (content.length < 50) {
            return Result.failure(IllegalArgumentException("Konten terlalu pendek untuk diringkas"))
        }
        return aiRepository.summarize(content)
    }
}

class ImproveWritingUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(content: String, style: WritingStyle = WritingStyle.NEUTRAL): Result<String> {
        if (content.isBlank()) {
            return Result.failure(IllegalArgumentException("Konten tidak boleh kosong"))
        }
        return aiRepository.improveWriting(content, style)
    }
}

class GenerateIdeasUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(topic: String): Result<List<String>> {
        if (topic.isBlank()) {
            return Result.failure(IllegalArgumentException("Topik tidak boleh kosong"))
        }
        return aiRepository.generateIdeas(topic)
    }
}
