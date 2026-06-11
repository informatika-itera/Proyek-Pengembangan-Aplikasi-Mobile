package id.pusakakata.domain.model

import kotlinx.datetime.Instant
import kotlin.test.*

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
    fun testSRSData_calculateNextReview_lowQuality() {
        val srs = SRSData()
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(2, now) // Quality < 3
        
        assertEquals(0, next.level)
        assertEquals(1, next.intervalDays)
        assertTrue(next.easeFactor < srs.easeFactor)
    }

    @Test
    fun testSRSData_calculateNextReview_highQuality_firstReview() {
        val srs = SRSData()
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(5, now) // Perfect
        
        assertEquals(1, next.level)
        assertEquals(1, next.intervalDays)
        assertEquals(2.6, next.easeFactor)
    }

    @Test
    fun testSRSData_calculateNextReview_highQuality_secondReview() {
        val srs = SRSData(intervalDays = 1, level = 1, easeFactor = 2.6)
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(4, now)
        
        assertEquals(2, next.level)
        assertEquals(6, next.intervalDays)
    }

    @Test
    fun testSRSData_calculateNextReview_minimumEaseFactor() {
        var srs = SRSData(easeFactor = 1.3)
        val now = Instant.fromEpochMilliseconds(0)
        srs = srs.calculateNextReview(0, now) 
        assertEquals(1.3, srs.easeFactor) 
    }

    @Test
    fun testSRSData_calculateNextReview_variousIntervals() {
        val now = Instant.fromEpochMilliseconds(0)
        
        // intervalDays == 0 -> 1
        val srs0 = SRSData(intervalDays = 0)
        assertEquals(1, srs0.calculateNextReview(3, now).intervalDays)
        
        // intervalDays == 1 -> 6
        val srs1 = SRSData(intervalDays = 1)
        assertEquals(6, srs1.calculateNextReview(3, now).intervalDays)
    }

    @Test
    fun testSRSData_calculateNextReview_quality4_easeFactorUnchanged() {
        val srs = SRSData(easeFactor = 2.5)
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(4, now)
        assertEquals(2.5, next.easeFactor)
    }

    @Test
    fun testSRSData_calculateNextReview_quality5_easeFactorIncreases() {
        val srs = SRSData(easeFactor = 2.5)
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(5, now)
        assertEquals(2.6, next.easeFactor)
    }

    @Test
    fun testSRSData_calculateNextReview_quality0_easeFactorDecreases() {
        val srs = SRSData(easeFactor = 2.5)
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(0, now)
        assertTrue(next.easeFactor < 2.5)
    }
}
