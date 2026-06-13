package com.example.neurodeck.presentation.screens.home

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit test untuk pure helper di HomeViewModel.kt:
 *   - [computeGreeting]   — pilih sapaan berdasarkan jam.
 *   - [computeTipOfTheDay] — pilih tip deterministik berdasarkan dayOfYear.
 *
 * Keduanya `internal` top-level function, jadi bisa diakses langsung dari
 * commonTest selama berada di package yang sama.
 */
class HomeLogicTest {

    private fun dateTimeAtHour(hour: Int): LocalDateTime =
        LocalDateTime(year = 2026, monthNumber = 5, dayOfMonth = 19, hour = hour, minute = 0)

    // ==================== computeGreeting ====================

    @Test
    fun `pagi untuk jam 4 sampai 10`() {
        assertEquals("Selamat Pagi", computeGreeting(dateTimeAtHour(4)))
        assertEquals("Selamat Pagi", computeGreeting(dateTimeAtHour(7)))
        assertEquals("Selamat Pagi", computeGreeting(dateTimeAtHour(10)))
    }

    @Test
    fun `siang untuk jam 11 sampai 14`() {
        assertEquals("Selamat Siang", computeGreeting(dateTimeAtHour(11)))
        assertEquals("Selamat Siang", computeGreeting(dateTimeAtHour(14)))
    }

    @Test
    fun `sore untuk jam 15 sampai 17`() {
        assertEquals("Selamat Sore", computeGreeting(dateTimeAtHour(15)))
        assertEquals("Selamat Sore", computeGreeting(dateTimeAtHour(17)))
    }

    @Test
    fun `malam untuk jam 18 sampai dini hari`() {
        assertEquals("Selamat Malam", computeGreeting(dateTimeAtHour(18)))
        assertEquals("Selamat Malam", computeGreeting(dateTimeAtHour(23)))
        assertEquals("Selamat Malam", computeGreeting(dateTimeAtHour(0)))
        assertEquals("Selamat Malam", computeGreeting(dateTimeAtHour(3)))
    }

    // ==================== computeTipOfTheDay ====================

    @Test
    fun `tip deterministik untuk tanggal yang sama`() {
        val date = dateTimeAtHour(9)
        assertEquals(
            computeTipOfTheDay(date),
            computeTipOfTheDay(date),
            "Tip harus sama untuk hari yang sama (deterministik)",
        )
    }

    @Test
    fun `tip selalu menghasilkan string non-kosong untuk berbagai tanggal`() {
        // Beberapa tanggal sepanjang tahun — semua harus mengembalikan tip valid.
        val dates = listOf(
            LocalDateTime(2026, 1, 1, 9, 0),
            LocalDateTime(2026, 3, 15, 9, 0),
            LocalDateTime(2026, 6, 30, 9, 0),
            LocalDateTime(2026, 9, 10, 9, 0),
            LocalDateTime(2026, 12, 31, 9, 0),
        )
        for (dt in dates) {
            assertTrue(computeTipOfTheDay(dt).isNotBlank())
        }
    }
}
