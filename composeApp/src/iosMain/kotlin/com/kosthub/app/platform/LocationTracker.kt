package com.kosthub.app.platform

actual class LocationTracker actual constructor(private val platformContext: PlatformContext) {
    actual suspend fun getCurrentLocation(): Pair<Double, Double>? {
        // Default coordinates for ITERA campus (Lampung, Indonesia)
        return Pair(-5.358, 105.314)
    }
}
