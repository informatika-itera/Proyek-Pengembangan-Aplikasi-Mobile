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
        val result = aiRepository.refineNote(note.rawContent)
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
}
