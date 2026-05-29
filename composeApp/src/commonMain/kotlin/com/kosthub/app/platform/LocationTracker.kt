package com.kosthub.app.platform

expect class LocationTracker(platformContext: PlatformContext) {
    suspend fun getCurrentLocation(): Pair<Double, Double>?
}
