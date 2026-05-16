package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.Announcement
import kotlinx.coroutines.flow.Flow

interface AnnouncementRepository {
    fun getAllAnnouncements(): Flow<List<Announcement>>
    suspend fun postAnnouncement(announcement: Announcement)
    suspend fun deleteAnnouncement(id: String)
}
