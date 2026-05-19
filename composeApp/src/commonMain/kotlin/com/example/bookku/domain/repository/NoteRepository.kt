package com.example.bookku.domain.repository

import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Book>>
    fun getPinnedNotes(): Flow<List<Book>>
    fun getBooksByCategory(category: BookGenre): Flow<List<Book>>
    fun searchNotes(query: String): Flow<List<Book>>
    fun getBookById(id: Long): Flow<Book?>
    suspend fun addBook(book: Book): Long
    suspend fun updateBook(book: Book)
    suspend fun deleteBook(id: Long)
    suspend fun togglePinNote(id: Long)
    suspend fun deleteBooks(ids: List<Long>)
}