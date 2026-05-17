package id.pusakakata.domain.model

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SRSDataTest {

    @Test
    fun testSRSData_initialState() {
        val srs = SRSData()
        assertEquals(0, srs.level)
        assertEquals(2.5, srs.easeFactor)
        assertEquals(0, srs.intervalDays)
        assertNull(srs.nextReview)
    }

    @Test
    fun testSRSData_customValues() {
        val next = Instant.fromEpochMilliseconds(1000)
        val srs = SRSData(
            intervalDays = 5,
            easeFactor = 2.8,
            nextReview = next,
            level = 3
        )
        assertEquals(3, srs.level)
        assertEquals(2.8, srs.easeFactor)
        assertEquals(5, srs.intervalDays)
        assertEquals(next, srs.nextReview)
    }
}
