package com.example.sholatyuk.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.context.GlobalContext
import java.util.Locale
import kotlin.coroutines.resume

actual class LocationService {

    private val context: Context
        get() = GlobalContext.get().get<Context>()

    @SuppressLint("MissingPermission")
    actual suspend fun getCurrentLocation(): LocationData? = suspendCancellableCoroutine { continuation ->
        // 1. Cek izin
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        // Jika GPS benar-benar mati, kembalikan null
        if (!isGpsEnabled && !isNetworkEnabled) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        // Fungsi bantuan untuk menerjemahkan kordinat dan mengembalikan data
        fun processLocation(loc: Location) {
            var cityName = "Lokasi Saat Ini" // <-- Fallback jitu agar tidak muncul Asia/Jakarta!
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    cityName = addresses[0].subAdminArea ?: addresses[0].locality ?: "Lokasi Saat Ini"
                }
            } catch (_: Exception) {}

            if (continuation.isActive) {
                continuation.resume(LocationData(loc.latitude, loc.longitude, cityName))
            }
        }

        val provider = if (isNetworkEnabled) LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER

        // 2. Coba ambil dari riwayat (Cache) dulu agar cepat
        val cachedLocation = locationManager.getLastKnownLocation(provider)
        if (cachedLocation != null) {
            processLocation(cachedLocation)
            return@suspendCancellableCoroutine
        }

        // 3. JIKA CACHE KOSONG (GPS baru dinyalakan), KITA PAKSA CARI SATELIT BARU!
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationManager.removeUpdates(this) // Berhenti melacak setelah dapat 1 titik
                processLocation(location)
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            // Perintah untuk memaksa Android mencari kordinat secara aktif
            locationManager.requestLocationUpdates(
                provider,
                0L,
                0f,
                locationListener,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resume(null)
        }

        // Jika user menutup aplikasi sebelum satelit terkunci, matikan sensor agar hemat baterai
        continuation.invokeOnCancellation {
            locationManager.removeUpdates(locationListener)
        }
    }

    actual fun hasLocationPermission(): Boolean {
        val fine = context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    // 👇 Fungsi baru untuk membuka layar Pengaturan GPS (Jembatan dari KMP ke OS Android)
    actual fun openLocationSettings() {
        val intent = android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}