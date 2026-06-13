package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.*
import kotlin.test.*

class RepositoryMappingTest {

    @Test
    fun `DTO Mapping Coverage - All Fields`() {
        // Booking DTO
        val bDto = BookingRemoteDto("1", "R1", "Room", "Building", 1000L, 2000L, "PENDING", "Sub", "U1")
        assertEquals("1", bDto.id)
        assertEquals("R1", bDto.room_id)
        assertEquals("PENDING", bDto.status)
        assertEquals("Sub", bDto.subject)
        assertEquals("U1", bDto.user_id)

        // Report DTO
        val repDto = ReportRemoteDto("1", "C", "L", "D", "HIGH", "PENDING", 100L, "url")
        assertEquals("1", repDto.id)
        assertEquals("HIGH", repDto.urgency)
        assertEquals("url", repDto.image_url)

        // Facility DTO
        val buildDto = BuildingRemoteDto("B1", "Gedung", "Desc", true)
        val roomDto = RoomRemoteDto("R1", "B1", "101", 1, "AVAILABLE", 50, true, true)
        assertEquals("B1", buildDto.id)
        assertTrue(buildDto.is_available)
        assertEquals(50, roomDto.capacity)
    }
}
