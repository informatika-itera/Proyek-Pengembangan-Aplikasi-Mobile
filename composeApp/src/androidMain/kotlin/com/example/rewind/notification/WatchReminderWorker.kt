package com.example.rewind.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.rewind.data.local.RewindDatabase
import com.example.rewind.data.local.entity.toMovie
import com.example.rewind.data.local.datastore.UserPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.flow.first 

class WatchReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val userPreferences: UserPreferences by inject()

    companion object {
        const val WORK_NAME = "watch_reminder_worker"
    }

    override suspend fun doWork(): Result {
        return try {
            val isEnabled = userPreferences.notificationsEnabled.first()

            if (!isEnabled) {
                return Result.success()
            }

            val driver = AndroidSqliteDriver(
                schema = RewindDatabase.Schema,
                context = context,
                name = "rewind.db"
            )
            val database = RewindDatabase(driver)
            val watchingMovies = database.movieQueries
                .getMoviesByStatus("WATCHING")
                .executeAsList()
                .map { it.toMovie() }

            if (watchingMovies.isNotEmpty()) {
                val notificationHelper = NotificationHelper(context)

                val (title, body) = if (watchingMovies.size == 1) {
                    val movie = watchingMovies.first()
                    val progress = if (movie.totalEpisodes != null && movie.totalEpisodes > 0) {
                        " Kamu sudah di episode ${movie.watchedEpisodes}/${movie.totalEpisodes}."
                    } else {
                        ""
                    }
                    "Lanjut Nonton 🎬" to "Lanjut nonton ${movie.title}!$progress"
                } else {
                    "Lanjut Nonton 🎬" to "Kamu punya ${watchingMovies.size} film yang belum selesai. Yuk lanjut nonton!"
                }

                notificationHelper.showWatchReminderNotification(title, body)
            }

            driver.close()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}