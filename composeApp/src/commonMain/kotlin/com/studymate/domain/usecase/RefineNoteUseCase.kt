package com.studymate.domain.usecase

import com.studymate.domain.model.Note
import com.studymate.domain.repository.AIRepository
import com.studymate.domain.repository.NoteRepository
import kotlinx.datetime.Clock

class RefineNoteUseCase(
    private val aiRepository: AIRepository,
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Result<Note> {
        val result = aiRepository.refineNote(
            subject = note.subject,
            title = note.title,
            content = note.rawContent
        )
        return result.mapCatching { refinedText ->
            val updatedNote = note.copy(
                refinedContent = refinedText,
                isRefined = true,
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
            noteRepository.updateNote(updatedNote)
            updatedNote
        }
    }

    suspend fun refineRawContent(subject: String, title: String, content: String): Result<String> {
        return aiRepository.refineNote(subject, title, content)
    }
}
