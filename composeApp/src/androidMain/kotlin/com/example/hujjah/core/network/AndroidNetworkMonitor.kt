package com.example.hujjah.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

class AndroidNetworkMonitor(
    private val context: Context
) : NetworkMonitor {
    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                trySend(false)
            }
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                trySend(hasInternet)
            }
        }
        
        // Initial state
        val initialStatus = try {
            connectivityManager.activeNetwork?.let { activeNetwork ->
                connectivityManager.getNetworkCapabilities(activeNetwork)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } ?: false
        } catch (e: SecurityException) {
            true // Fallback to online to avoid crashes if permission is not granted
        } catch (e: Exception) {
            true
        }
        trySend(initialStatus)
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        var registered = false
        try {
            connectivityManager.registerNetworkCallback(request, callback)
            registered = true
        } catch (e: SecurityException) {
            trySend(true)
        } catch (e: Exception) {
            trySend(true)
        }
        
        awaitClose {
            if (registered) {
                try {
                    connectivityManager.unregisterNetworkCallback(callback)
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
    }.conflate()
}
