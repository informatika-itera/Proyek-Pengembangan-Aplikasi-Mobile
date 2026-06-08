package id.my.sinanonym.mybawanggacha.data.local.source

import id.my.sinanonym.mybawanggacha.core.coroutines.AppDispatchers
import id.my.sinanonym.mybawanggacha.data.local.NoteDatabase
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

    suspend fun getMarkedEpisodeNumbers(animeId: Int): Set<Int> = withContext(dispatchers.io) {
        queries.getMarkedEpisodeNumbers(animeId.toLong())
            .executeAsList()
            .map { it.toInt() }
            .toSet()
    }

    suspend fun setEpisodeWatched(
        animeId: Int,
        episodeNumber: Int,
        watched: Boolean
    ) = withContext(dispatchers.io) {
        queries.upsertEpisodeWatched(
            anime_id = animeId.toLong(),
            episode_number = episodeNumber.toLong(),
            watched = if (watched) 1L else 0L,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    suspend fun setEpisodeMarked(
        animeId: Int,
        episodeNumber: Int,
        marked: Boolean
    ) = withContext(dispatchers.io) {
        queries.upsertEpisodeMarked(
            anime_id = animeId.toLong(),
            episode_number = episodeNumber.toLong(),
            marked = if (marked) 1L else 0L,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }
}
