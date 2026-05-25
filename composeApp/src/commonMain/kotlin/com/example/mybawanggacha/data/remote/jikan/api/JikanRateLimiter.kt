package com.example.mybawanggacha.data.remote.jikan.api

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.collections.ArrayDeque
import kotlin.time.Clock

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

                while (requestTimestamps.firstOrNull()?.let { now - it >= ONE_MINUTE_MS } == true) {
                    requestTimestamps.removeFirst()
                }

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
    internal fun resetForTest(enabled: Boolean = true) {
        this.enabled = enabled
        lastRequestAt = 0L
        requestTimestamps.clear()
    }
}
