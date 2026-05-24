package com.example.noteai.domain.usecase

import com.example.noteai.domain.model.Note
import com.example.noteai.domain.model.VulnSeverity
import com.example.noteai.domain.repository.AIRepository
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.domain.repository.WritingStyle
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
            NoteSortBy.SEVERITY_DESC -> notes.sortedByDescending { it.severity.ordinal }
        }
    }
}

enum class NoteSortBy(val displayName: String) {
    TITLE_ASC("Judul (A-Z)"),
    TITLE_DESC("Judul (Z-A)"),
    CREATED_ASC("Dibuat (Lama)"),
    CREATED_DESC("Dibuat (Baru)"),
    UPDATED_ASC("Diupdate (Lama)"),
    UPDATED_DESC("Diupdate (Baru)"),
    SEVERITY_DESC("Severity (Tertinggi)")
}

class SearchNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(query: String, severity: VulnSeverity? = null): Flow<List<Note>> {
        return if (query.isBlank() && severity == null) {
            repository.getAllNotes()
        } else if (query.isBlank()) {
            repository.getNotesBySeverity(severity!!)
        } else {
            repository.searchNotes(query).map { notes ->
                if (severity != null) {
                    notes.filter { it.severity == severity }
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
                return Result.failure(IllegalArgumentException("Log temuan tidak boleh kosong"))
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

class GenerateVDPReportUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(
        title: String,
        targetUrl: String,
        vulnType: String,
        severity: String,
        description: String
    ): Result<String> {
        if (description.isBlank()) {
            return Result.failure(IllegalArgumentException("Deskripsi temuan tidak boleh kosong"))
        }
        return aiRepository.generateVDPReport(title, targetUrl, vulnType, severity, description)
    }
}
