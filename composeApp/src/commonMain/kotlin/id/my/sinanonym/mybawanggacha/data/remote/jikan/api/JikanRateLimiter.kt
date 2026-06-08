package id.my.sinanonym.mybawanggacha.data.remote.jikan.api

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.collections.ArrayDeque
import kotlin.time.Clock

internal data class JikanRequestUsageSnapshot(
    val usedLastSecond: Int,
    val secondLimit: Int,
    val usedLastMinute: Int,
    val minuteLimit: Int,
    val msUntilNextRequest: Long,
    val enabled: Boolean
) {
    val remainingThisMinute: Int
        get() = (minuteLimit - usedLastMinute).coerceAtLeast(0)

    companion object {
        val Empty = JikanRequestUsageSnapshot(
            usedLastSecond = 0,
            secondLimit = 3,
            usedLastMinute = 0,
            minuteLimit = 60,
            msUntilNextRequest = 0L,
            enabled = true
        )
    }
}

/**
 * Shared limiter for every Jikan call made by this app.
 *
 * Jikan public limit:
 * - 3 requests / second
 * - 60 requests / minute
 */
internal object JikanRateLimiter {
    private const val MIN_REQUEST_INTERVAL_MS = 360L
    private const val MAX_REQUESTS_PER_MINUTE = 60
    private const val ONE_MINUTE_MS = 60_000L

    private val mutex = Mutex()
    private val requestTimestamps = ArrayDeque<Long>()
    private var lastRequestAt = 0L
    private var enabled = true

    suspend fun awaitTurn() {
        if (!enabled) return

        mutex.withLock {
            var acquired = false

            while (!acquired) {
                val now = Clock.System.now().toEpochMilliseconds()

                pruneExpiredRequests(now)

                val secondWaitMs = (lastRequestAt + MIN_REQUEST_INTERVAL_MS - now).coerceAtLeast(0L)
                val minuteWaitMs = if (requestTimestamps.size >= MAX_REQUESTS_PER_MINUTE) {
                    (requestTimestamps.first() + ONE_MINUTE_MS - now).coerceAtLeast(0L)
                } else {
                    0L
                }
                val waitMs = maxOf(secondWaitMs, minuteWaitMs)

                if (waitMs <= 0L) {
                    val timestamp = Clock.System.now().toEpochMilliseconds()
                    lastRequestAt = timestamp
                    requestTimestamps.addLast(timestamp)
                    acquired = true
                } else {
                    delay(waitMs)
                }
            }
        }
    }

    internal suspend fun snapshot(): JikanRequestUsageSnapshot {
        return mutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()
            pruneExpiredRequests(now)
            buildSnapshot(now)
        }
    }

    internal fun resetForTest(enabled: Boolean = true) {
        this.enabled = enabled
        lastRequestAt = 0L
        requestTimestamps.clear()
    }

    private fun pruneExpiredRequests(now: Long) {
        while (requestTimestamps.firstOrNull()?.let { timestamp ->
                now - timestamp >= ONE_MINUTE_MS
            } == true
        ) {
            requestTimestamps.removeFirst()
        }
    }

    private fun buildSnapshot(now: Long): JikanRequestUsageSnapshot {
        val usedLastSecond = requestTimestamps.count { timestamp ->
            now - timestamp < 1_000L
        }
        val msUntilNextRequest = (lastRequestAt + MIN_REQUEST_INTERVAL_MS - now).coerceAtLeast(0L)

        return JikanRequestUsageSnapshot(
            usedLastSecond = usedLastSecond,
            secondLimit = 3,
            usedLastMinute = requestTimestamps.size,
            minuteLimit = MAX_REQUESTS_PER_MINUTE,
            msUntilNextRequest = msUntilNextRequest,
            enabled = enabled
        )
    }
}
