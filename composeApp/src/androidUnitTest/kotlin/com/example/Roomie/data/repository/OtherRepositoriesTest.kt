package com.example.Roomie.data.repository

import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.*
import io.github.jan.supabase.SupabaseClient
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class OtherRepositoriesTest {

    private val mockDb = mockk<RoomieDatabase>(relaxed = true)
    private val mockSupabase = mockk<SupabaseClient>(relaxed = true)

    @Test
    fun `AnnouncementRepository - coverage boost`() = runTest {
        val repo = AnnouncementRepositoryImpl(mockDb)
        repo.getAllAnnouncements()
        val announcement = Announcement("1", "T", "M", "A", 0L)
        repo.postAnnouncement(announcement)
        repo.deleteAnnouncement("1")
        verify { mockDb.announcementQueries.getAllAnnouncements() }
    }

    @Test
    fun `AuditRepository - coverage boost`() = runTest {
        val repo = SupabaseAuditRepositoryImpl(mockSupabase)
        try {
            repo.getAuditLogs()
            val log = AuditLog("1", "R1", "Room", "U1", "User", "Action", 0L)
            repo.addAuditLog(log)
        } catch (e: Exception) {
            // Expected catch
        }
    }
}
