package com.example.noteai.data.repository.hujjah

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.noteai.data.local.NoteDatabase
import com.example.noteai.data.local.entity.toBookmarkDomainList
import com.example.noteai.data.local.entity.toBookmarkEntityValues
import com.example.noteai.data.local.entity.toDomain
import com.example.noteai.domain.model.islamic.BookmarkReference
import com.example.noteai.domain.model.islamic.IslamicReference
import com.example.noteai.domain.repository.hujjah.BookmarkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class BookmarkRepositoryImpl(
    private val database: NoteDatabase
) : BookmarkRepository {

    private val queries = database.bookmarkQueries

    override fun getBookmarks(): Flow<List<BookmarkReference>> {
        return queries.getAllBookmarks()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toBookmarkDomainList() }
    }

    override fun getBookmarkByReferenceId(referenceId: String): Flow<BookmarkReference?> {
        return queries.getBookmarkByReferenceId(referenceId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity -> entity?.toDomain() }
    }

    override suspend fun saveBookmark(reference: IslamicReference, note: String) = withContext(Dispatchers.Default) {
        val values = reference.toBookmarkEntityValues(
            note = note,
            savedAt = Clock.System.now().toEpochMilliseconds()
        )

        queries.insertBookmark(
            reference_id = values.referenceId,
            source_type = values.sourceType,
            title = values.title,
            source_name = values.sourceName,
            arabic_text = values.arabicText,
            translation = values.translation,
            explanation = values.explanation,
            topic_id = values.topicId,
            topic_title = values.topicTitle,
            note = values.note,
            saved_at = values.savedAt
        )
    }

    override suspend fun updateBookmarkNote(referenceId: String, note: String) = withContext(Dispatchers.Default) {
        queries.updateBookmarkNote(
            note = note,
            reference_id = referenceId
        )
    }

    override suspend fun deleteBookmark(referenceId: String) = withContext(Dispatchers.Default) {
        queries.deleteBookmarkByReferenceId(referenceId)
    }
}
