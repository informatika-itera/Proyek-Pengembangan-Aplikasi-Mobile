package com.example.Roomie.core.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Placeholder implementation for iOS Network Monitor.
 * In a real iOS project, this would use NWPathMonitor via platform-specific code.
 */
class IosNetworkMonitor : NetworkMonitor {
    private val _isOnline = MutableStateFlow(true) // Default to true for simulator/placeholder
    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
}
