package com.example.mybawanggacha.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.mybawanggacha.data.local.LibraryEntryEntity
import com.example.mybawanggacha.data.local.NoteDatabase
import com.example.mybawanggacha.domain.model.LibraryEntry
import com.example.mybawanggacha.domain.model.LibraryStatus
import com.example.mybawanggacha.domain.model.MediaType
import com.example.mybawanggacha.domain.model.UserProgress
import com.example.mybawanggacha.domain.model.UserScore
import com.example.mybawanggacha.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Instant

class LibraryRepositoryImpl(
    private val database: NoteDatabase
) : LibraryRepository {

    private val queries = database.animeQueries

    override fun observeEntries(): Flow<List<LibraryEntry>> {
        return queries.getLibraryEntries()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeEntriesByStatus(status: LibraryStatus): Flow<List<LibraryEntry>> {
        return queries.getLibraryEntriesByStatus(status.storageKey)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeEntry(mediaId: Int, mediaType: MediaType): Flow<LibraryEntry?> {
        return queries.getLibraryEntryByMedia(
            media_id = mediaId.toLong(),
            media_type = mediaType.storageKey
        )
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { row -> row?.toDomain() }
    }

    override suspend fun getEntryById(id: Long): LibraryEntry? = withContext(Dispatchers.Default) {
        queries.getLibraryEntryById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun getEntry(mediaId: Int, mediaType: MediaType): LibraryEntry? = withContext(Dispatchers.Default) {
        queries.getLibraryEntryByMedia(
            media_id = mediaId.toLong(),
            media_type = mediaType.storageKey
        ).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun upsertEntry(entry: LibraryEntry): Long = withContext(Dispatchers.Default) {
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
        withContext(Dispatchers.Default) {
            queries.deleteLibraryEntryById(id)
        }
    }

    override suspend fun deleteEntry(mediaId: Int, mediaType: MediaType) {
        withContext(Dispatchers.Default) {
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
