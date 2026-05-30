package com.kosthub.app.platform

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.delay
import kotlin.coroutines.resume

actual class LocationTracker actual constructor(private val platformContext: PlatformContext) {

    actual suspend fun getCurrentLocation(): Pair<Double, Double>? {
        val context = platformContext.context
        
        // Check permissions
        var hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        var hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        if (!hasFine && !hasCoarse) {
            val activity = context as? Activity
            if (activity != null) {
                // Request permissions
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    1001
                )
                
                // Wait for the user to grant permissions (poll for up to 10 seconds or until granted)
                var attempts = 0
                while (attempts < 40) { // 40 * 250ms = 10s max wait
                    delay(250)
                    hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (hasFine || hasCoarse) {
                        break
                    }
                    attempts++
                }
            }
        }
        
        // Re-check permissions
        hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFine && !hasCoarse) {
            return null
        }
        
        return getActualLocation()
    }

    @SuppressLint("MissingPermission")
    private suspend fun getActualLocation(): Pair<Double, Double>? {
        val context = platformContext.context
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

        // 1. Try last known location first from all enabled providers
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (prov in providers) {
            val loc = try {
                locationManager.getLastKnownLocation(prov)
            } catch (e: SecurityException) {
                null
            }
            if (loc != null) {
                if (bestLocation == null || loc.time > bestLocation.time) {
                    bestLocation = loc
                }
            }
        }
        if (bestLocation != null) {
            return Pair(bestLocation.latitude, bestLocation.longitude)
        }

        // 2. If no last known location, request fresh updates with an 8 second timeout
        return kotlinx.coroutines.withTimeoutOrNull(8000) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        try {
                            locationManager.removeUpdates(this)
                        } catch (e: Exception) {}
                        if (continuation.isActive) {
                            continuation.resume(Pair(location.latitude, location.longitude))
                        }
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                var registeredAny = false
                for (prov in providers) {
                    try {
                        locationManager.requestLocationUpdates(
                            prov,
                            0L,
                            0f,
                            listener,
                            context.mainLooper
                        )
                        registeredAny = true
                    } catch (e: Exception) {
                        // ignore and try next
                    }
                }

                if (!registeredAny) {
                    continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    try {
                        locationManager.removeUpdates(listener)
                    } catch (e: Exception) {}
                }
            }
        }
    }
}
