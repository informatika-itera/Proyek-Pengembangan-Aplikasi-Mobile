package com.studyhub.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class IosNetworkMonitor : NetworkMonitor {
    override val isOnline: Flow<Boolean> =
        MutableStateFlow(true)
        // iOS network monitoring — simplified untuk KMP
        // Full implementation menggunakan NWPathMonitor
}
