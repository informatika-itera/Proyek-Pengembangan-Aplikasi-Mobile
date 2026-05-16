package com.example.Roomie.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Announcement
import com.example.Roomie.domain.repository.AnnouncementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AnnouncementRepositoryImpl(
    database: RoomieDatabase
) : AnnouncementRepository {
    private val queries = database.announcementQueries

    override fun getAllAnnouncements(): Flow<List<Announcement>> {
        return queries.getAllAnnouncements()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Announcement(
                        id = entity.id,
                        title = entity.title,
                        message = entity.message,
                        author = entity.author,
                        createdAt = entity.createdAt
                    )
                }
            }
    }

    override suspend fun postAnnouncement(announcement: Announcement) {
        withContext(Dispatchers.IO) {
            queries.insertAnnouncement(
                id = announcement.id,
                title = announcement.title,
                message = announcement.message,
                author = announcement.author,
                createdAt = announcement.createdAt
            )
        }
    }

    override suspend fun deleteAnnouncement(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteAnnouncement(id)
        }
    }
}
