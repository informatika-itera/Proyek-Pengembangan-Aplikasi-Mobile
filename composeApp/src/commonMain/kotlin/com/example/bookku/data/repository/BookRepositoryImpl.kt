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
import com.example.bookku.domain.model.SearchFilter
import com.example.bookku.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class BookRepositoryImpl(private val database: BookDatabase) : NoteRepository {

    private val queries = database.bookQueries

    override fun getAllNotes(): Flow<List<Book>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.toDomainList() }
    }

    override fun getNotesByUser(userId: String): Flow<List<Book>> {
        return queries.getNotesByUser(userId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.toDomainList() }
    }

    override fun getPinnedNotes(): Flow<List<Book>> {
        return queries.getPinnedNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.toDomainList() }
    }

    override fun getBooksByCategory(category: BookGenre): Flow<List<Book>> {
        return queries.getNotesByCategory(category.name)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.toDomainList() }
    }

    override fun searchNotes(query: String): Flow<List<Book>> {
        return queries.searchNotes(query, query, query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.toDomainList() }
    }

    override fun searchNotesWithFilter(filter: SearchFilter): Flow<List<Book>> = flow {
        val books = if (filter.query.isNotBlank()) {
            queries.searchNotes(filter.query, filter.query, filter.query)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { entities -> entities.toDomainList() }
                .first()
        } else {
            queries.getAllNotes()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { entities -> entities.toDomainList() }
                .first()
        }

        var filtered = books

        if (filter.category != null) {
            filtered = filtered.filter { it.category == filter.category }
        }

        if (filter.isPinnedOnly) {
            filtered = filtered.filter { it.isPinned }
        }

        emit(filtered)
    }

    override fun getBookById(id: Long): Flow<Book?> {
        return queries.getBookById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity -> entity?.toDomain() }
    }

    override suspend fun addBook(book: Book): Long = withContext(Dispatchers.IO) {
        val values = book.toEntityValues()
        queries.addBook(
            user_id = values.userId,
            title = values.title,
            content = values.content,
            author = values.author,
            cover_url = values.coverUrl,
            category = values.category,
            color = values.color,
            total_pages = values.totalPages.toLong(),
            is_pinned = values.isPinned,
            created_at = values.createdAt,
            updated_at = values.updatedAt
        )
        queries.lastInsertId().executeAsOne()
    }

    override suspend fun updateBook(book: Book) = withContext(Dispatchers.IO) {
        val values = book.toEntityValues()
        val now = Clock.System.now().toEpochMilliseconds()
        queries.updateBook(
            title = values.title,
            content = values.content,
            author = values.author,
            cover_url = values.coverUrl,
            category = values.category,
            color = values.color,
            total_pages = values.totalPages.toLong(),
            is_pinned = values.isPinned,
            updated_at = now,
            id = book.id
        )
    }

    override suspend fun deleteBook(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteNoteById(id)
    }

    override suspend fun togglePinNote(id: Long) = withContext(Dispatchers.IO) {
        queries.togglePin(
            updated_at = Clock.System.now().toEpochMilliseconds(),
            id = id
        )
    }

    override suspend fun deleteBooks(ids: List<Long>) = withContext(Dispatchers.IO) {
        queries.deleteNotesByIds(ids)
    }

    override fun getCachedRecommendation(bookId: Long): Flow<String?> {
        return queries.getRecommendationForBook(bookId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.recommendation_text }
    }

    override suspend fun saveRecommendation(bookId: Long, recommendation: String) = withContext(Dispatchers.IO) {
        queries.insertRecommendation(
            book_id = bookId,
            recommendation_text = recommendation,
            created_at = Clock.System.now().toEpochMilliseconds()
        )
    }
}
