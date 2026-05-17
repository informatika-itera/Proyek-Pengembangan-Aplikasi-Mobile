package com.soundletter.app.domain.repository

import com.soundletter.app.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface LetterRepository {
    fun getLetters(): Flow<List<Note>>
    suspend fun getLetterById(id: Long): Note?
    suspend fun sendLetter(letter: Note)
    suspend fun deleteLetter(id: Long)
}
