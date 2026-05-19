package com.example.bookku.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.bookku.data.local.BookDatabase
import com.example.bookku.data.local.entity.toDomain
import com.example.bookku.data.local.entity.toDomainList
import com.example.bookku.data.local.entity.toEntityValues
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class NoteRepositoryImpl(private val database: BookDatabase) : NoteRepository {

    private val queries = database.bookQueries

    override fun getAllNotes(): Flow<List<Book>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }

    override fun getPinnedNotes(): Flow<List<Book>> {
        return queries.getPinnedNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }

    override fun getBooksByCategory(category: BookGenre): Flow<List<Book>> {
        return queries.getNotesByCategory(category.name)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }

    override fun searchNotes(query: String): Flow<List<Book>> {
        return queries.searchNotes(query, query)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }

    override fun getBookById(id: Long): Flow<Book?> {
        return queries.getBookById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity -> entity?.toDomain() }
    }

    override suspend fun addBook(book: Book): Long = withContext(Dispatchers.Default) {
        val values = book.toEntityValues()
        queries.addBook(
            title = values.title,
            content = values.content,
            category = values.category,
            color = values.color,
            is_pinned = values.isPinned,
            created_at = values.createdAt,
            updated_at = values.updatedAt
        )
        queries.lastInsertId().executeAsOne()
    }

    override suspend fun updateBook(book: Book) = withContext(Dispatchers.Default) {
        val values = book.toEntityValues()
        queries.updateBook(
            id = book.id,
            title = values.title,
            content = values.content,
            category = values.category,
            color = values.color,
            is_pinned = values.isPinned,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun deleteBook(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteNoteById(id)
    }

    override suspend fun togglePinNote(id: Long) = withContext(Dispatchers.Default) {
        queries.togglePin(
            id = id,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun deleteBooks(ids: List<Long>) = withContext(Dispatchers.Default) {
        queries.deleteNotesByIds(ids)
    }
}
