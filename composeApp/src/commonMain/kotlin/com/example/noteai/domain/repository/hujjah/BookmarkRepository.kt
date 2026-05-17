package com.example.noteai.domain.repository.hujjah

import com.example.noteai.domain.model.islamic.BookmarkReference
import com.example.noteai.domain.model.islamic.IslamicReference
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmarks(): Flow<List<BookmarkReference>>
    fun getBookmarkByReferenceId(referenceId: String): Flow<BookmarkReference?>
    suspend fun saveBookmark(reference: IslamicReference, note: String = "")
    suspend fun updateBookmarkNote(referenceId: String, note: String)
    suspend fun deleteBookmark(referenceId: String)
}
