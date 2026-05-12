package com.example.pantaujompo

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

actual class PlatformLocationProvider {
    @SuppressLint("MissingPermission")
    actual fun requestSingleUpdate(callback: (Double, Double) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(PantauJompoApplication.appContext)
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    callback(location.latitude, location.longitude)
                } else {
                    callback(-5.3582, 105.3149)
                }
            }
    }
}