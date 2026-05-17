package com.example.mybawanggacha.domain.library.repository

import com.example.mybawanggacha.domain.library.model.LibraryEntry
import com.example.mybawanggacha.domain.library.model.LibraryStatus
import com.example.mybawanggacha.domain.library.model.MediaType
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun observeEntries(): Flow<List<LibraryEntry>>
    fun observeEntriesByStatus(status: LibraryStatus): Flow<List<LibraryEntry>>
    fun observeEntry(mediaId: Int, mediaType: MediaType): Flow<LibraryEntry?>
    suspend fun getEntryById(id: Long): LibraryEntry?
    suspend fun getEntry(mediaId: Int, mediaType: MediaType): LibraryEntry?
    suspend fun upsertEntry(entry: LibraryEntry): Long
    suspend fun deleteEntry(id: Long)
    suspend fun deleteEntry(mediaId: Int, mediaType: MediaType)
}