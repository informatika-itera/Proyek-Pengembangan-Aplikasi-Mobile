package com.example.mybawanggacha.domain.repository

import com.example.mybawanggacha.domain.model.LibraryEntry
import com.example.mybawanggacha.domain.model.LibraryStatus
import com.example.mybawanggacha.domain.model.MediaType
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
