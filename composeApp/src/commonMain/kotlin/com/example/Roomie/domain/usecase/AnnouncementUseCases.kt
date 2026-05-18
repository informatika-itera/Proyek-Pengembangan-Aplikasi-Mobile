package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.Announcement
import com.example.Roomie.domain.repository.AnnouncementRepository
import kotlinx.coroutines.flow.Flow

class GetAllAnnouncementsUseCase(private val repository: AnnouncementRepository) {
    operator fun invoke(): Flow<List<Announcement>> = repository.getAllAnnouncements()
}

class PostAnnouncementUseCase(private val repository: AnnouncementRepository) {
    suspend operator fun invoke(announcement: Announcement) = repository.postAnnouncement(announcement)
}
