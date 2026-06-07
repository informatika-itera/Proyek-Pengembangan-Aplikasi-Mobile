package com.example.Roomie.domain.usecase.ai

import com.example.Roomie.data.remote.ai.ExtractionResult
import com.example.Roomie.data.remote.ai.GeminiService
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.repository.BookingRepository
import com.example.Roomie.domain.repository.FacilityRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.*

/**
 * UseCase untuk memproses input natural language menjadi hasil pencarian ruangan
 */
open class ProcessAICommandUseCase(
    private val geminiService: GeminiService,
    private val facilityRepository: FacilityRepository,
    private val bookingRepository: BookingRepository
) {
    open suspend operator fun invoke(userInput: String): AIResult {
        // 1. Ekstrak data pake Gemini
        val extraction = geminiService.processUserCommand(userInput) 
            ?: return AIResult.Error("Gagal memproses permintaan lo, bre. Coba lagi ya!")

        // 2. Validasi minimal data (Harus ada kapasitas atau tanggal)
        if (extraction.capacity == null && extraction.date == null && extraction.buildingName == null) {
            return AIResult.Error("Datanya kurang lengkap nih, bre. Sebutin gedung, kapasitas, atau tanggalnya ya.")
        }

        // 3. Ambil SEMUA data untuk filtering manual (Biar Singkron & Fleksibel)
        // Pake searchRooms("") biar narik semua data tanpa filter gedung di awal
        val allRooms = facilityRepository.searchRooms("").first()
        val allBookings = bookingRepository.getAllBookings().first()

        val tz = TimeZone.currentSystemDefault()
        
        // Parsing Waktu dari AI (Handling Nulls)
        val requestedDate = extraction.date?.let { LocalDate.parse(it) } ?: Clock.System.now().toLocalDateTime(tz).date
        val startTime = extraction.startTime?.let { LocalTime.parse(it) } ?: LocalTime(8, 0)
        val endTime = extraction.endTime?.let { LocalTime.parse(it) } ?: LocalTime(17, 0)
        
        val requestedStartMs = LocalDateTime(requestedDate, startTime).toInstant(tz).toEpochMilliseconds()
        val requestedEndMs = LocalDateTime(requestedDate, endTime).toInstant(tz).toEpochMilliseconds()

        // 4. Filtering Cerdas
        val filteredRooms = allRooms.filter { room ->
            // A. Filter Gedung (Case Insensitive & Flexible)
            val buildingMatch = extraction.buildingName?.let { 
                val cleanInput = it.lowercase().replace(" ", "")
                room.buildingId.lowercase().contains(cleanInput) || 
                room.id.lowercase().contains(cleanInput)
            } ?: true

            // B. Filter Kapasitas
            val capacityMatch = room.capacity >= (extraction.capacity ?: 0)

            // C. Filter Maintenance
            val isHealthy = room.status != RoomStatus.MAINTENANCE

            // D. Filter Availability (Cek Jadwal)
            val isAvailable = allBookings.none { booking ->
                booking.roomId == room.id && 
                booking.status == BookingStatus.APPROVED &&
                booking.startTime < requestedEndMs && requestedStartMs < booking.endTime
            }

            buildingMatch && capacityMatch && isHealthy && isAvailable
        }

        return AIResult.Success(
            extractedData = extraction,
            suggestedRooms = filteredRooms.take(5) // Limit top 5
        )
    }
}

sealed interface AIResult {
    data class Success(
        val extractedData: ExtractionResult,
        val suggestedRooms: List<Room>
    ) : AIResult
    data class Error(val message: String) : AIResult
}
