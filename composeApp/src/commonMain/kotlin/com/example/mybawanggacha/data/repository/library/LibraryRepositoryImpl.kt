package com.example.mybawanggacha.data.repository.library

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.local.LibraryEntryEntity
import com.example.mybawanggacha.data.local.NoteDatabase
import com.example.mybawanggacha.domain.library.model.LibraryEntry
import com.example.mybawanggacha.domain.library.model.LibraryStatus
import com.example.mybawanggacha.domain.library.model.MediaType
import com.example.mybawanggacha.domain.library.model.UserProgress
import com.example.mybawanggacha.domain.library.model.UserScore
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Instant

class LibraryRepositoryImpl(
    private val database: NoteDatabase,
    private val dispatchers: AppDispatchers
) : LibraryRepository {

    private val queries = database.libraryQueries

    override fun observeEntries(): Flow<List<LibraryEntry>> {
        return queries.getLibraryEntries()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeEntriesByStatus(status: LibraryStatus): Flow<List<LibraryEntry>> {
        return queries.getLibraryEntriesByStatus(status.storageKey)
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeEntry(mediaId: Int, mediaType: MediaType): Flow<LibraryEntry?> {
        return queries.getLibraryEntryByMedia(
            media_id = mediaId.toLong(),
            media_type = mediaType.storageKey
        )
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .map { row -> row?.toDomain() }
    }

    override suspend fun getEntryById(id: Long): LibraryEntry? = withContext(dispatchers.io) {
        queries.getLibraryEntryById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun getEntry(mediaId: Int, mediaType: MediaType): LibraryEntry? = withContext(dispatchers.io) {
        queries.getLibraryEntryByMedia(
            media_id = mediaId.toLong(),
            media_type = mediaType.storageKey
        ).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun upsertEntry(entry: LibraryEntry): Long = withContext(dispatchers.io) {
        val now = Clock.System.now().toEpochMilliseconds()
        val existing = if (entry.id > 0L) {
            queries.getLibraryEntryById(entry.id).executeAsOneOrNull()
        } else {
            queries.getLibraryEntryByMedia(
                media_id = entry.mediaId.toLong(),
                media_type = entry.mediaType.storageKey
            ).executeAsOneOrNull()
        }

        if (existing == null) {
            queries.insertLibraryEntry(
                media_id = entry.mediaId.toLong(),
                media_type = entry.mediaType.storageKey,
                title = entry.title,
                image_url = entry.imageUrl,
                status = entry.status.storageKey,
                progress = entry.progress.current.toLong(),
                total_count = entry.progress.total?.toLong(),
                user_score = entry.userScore?.value?.toLong(),
                notes = entry.notes?.takeIf { it.isNotBlank() },
                created_at = now,
                updated_at = now
            )
            queries.lastLibraryInsertId().executeAsOne()
        } else {
            queries.updateLibraryEntry(
                title = entry.title,
                image_url = entry.imageUrl,
                status = entry.status.storageKey,
                progress = entry.progress.current.toLong(),
                total_count = entry.progress.total?.toLong(),
                user_score = entry.userScore?.value?.toLong(),
                notes = entry.notes?.takeIf { it.isNotBlank() },
                updated_at = now,
                id = existing.id
            )
            existing.id
        }
    }

    override suspend fun deleteEntry(id: Long) {
        withContext(dispatchers.io) {
            queries.deleteLibraryEntryById(id)
        }
    }

    override suspend fun deleteEntry(mediaId: Int, mediaType: MediaType) {
        withContext(dispatchers.io) {
            queries.deleteLibraryEntryByMedia(
                media_id = mediaId.toLong(),
                media_type = mediaType.storageKey
            )
        }
    }
}

private fun LibraryEntryEntity.toDomain(): LibraryEntry {
    return LibraryEntry(
        id = id,
        mediaId = media_id.toInt(),
        mediaType = MediaType.fromStorageKey(media_type),
        title = title,
        imageUrl = image_url,
        status = LibraryStatus.fromStorageKey(status),
        progress = UserProgress(
            current = progress.toInt(),
            total = total_count?.toInt()
        ),
        userScore = user_score?.toInt()?.let(::UserScore),
        notes = notes,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}
