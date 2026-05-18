package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.Announcement
import com.example.Roomie.domain.repository.AnnouncementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeAnnouncementRepository : AnnouncementRepository {
    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    
    override fun getAllAnnouncements(): Flow<List<Announcement>> = _announcements

    override suspend fun postAnnouncement(announcement: Announcement) {
        _announcements.update { it + announcement }
    }

    override suspend fun deleteAnnouncement(id: String) {
        _announcements.update { current ->
            current.filter { it.id != id }
        }
    }
    
    fun setAnnouncements(list: List<Announcement>) {
        _announcements.value = list
    }
}
