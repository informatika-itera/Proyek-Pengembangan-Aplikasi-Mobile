package com.example.mybawanggacha.data.local.source

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.local.NoteDatabase
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class AnimeProgressLocalDataSource(
    database: NoteDatabase,
    private val dispatchers: AppDispatchers
) {
    private val queries = database.animeQueries

    suspend fun getWatchedEpisodeNumbers(animeId: Int): Set<Int> = withContext(dispatchers.io) {
        queries.getWatchedEpisodeNumbers(animeId.toLong())
            .executeAsList()
            .map { it.toInt() }
            .toSet()
    }

    suspend fun setEpisodeWatched(
        animeId: Int,
        episodeNumber: Int,
        watched: Boolean
    ) = withContext(dispatchers.io) {
        queries.upsertEpisodeProgress(
            anime_id = animeId.toLong(),
            episode_number = episodeNumber.toLong(),
            watched = if (watched) 1L else 0L,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }
}
