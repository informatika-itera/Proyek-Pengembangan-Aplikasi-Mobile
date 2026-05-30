package com.example.travelplanner.core.util

import kotlinx.coroutines.flow.Flow

/**
 * Common interface for network connectivity monitoring.
 * Android implementation: NetworkMonitor (ConnectivityManager).
 */
interface NetworkMonitorInterface {
    val isOnline: Flow<Boolean>
}
