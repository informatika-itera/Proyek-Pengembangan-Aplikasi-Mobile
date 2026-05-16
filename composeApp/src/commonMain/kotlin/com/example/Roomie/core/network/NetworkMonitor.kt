package com.example.Roomie.core.network

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface untuk memantau status koneksi internet secara reaktif.
 */
interface NetworkMonitor {
    val isOnline: StateFlow<Boolean>
}
