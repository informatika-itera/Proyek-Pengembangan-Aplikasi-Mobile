package com.example.pantaujompo.core.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.pantaujompo.domain.TrackingManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.Locale

class LocationTrackingService : Service() {

    private val CHANNEL_ID = "TrackingChannel"
    private val NOTIFICATION_ID = 1

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var previousLocation: Location? = null

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!TrackingManager.isTracking.value) return
                for (location in locationResult.locations) {
                    if (location.accuracy > 15f) continue
                    val currentGeoPoint = GeoPoint(location.latitude, location.longitude)

                    if (previousLocation == null) {
                        previousLocation = location
                        TrackingManager.addLocationPoint(currentGeoPoint, 0.0)
                        continue
                    }

                    previousLocation?.let { lastLoc ->
                        val distanceDelta = lastLoc.distanceTo(location).toDouble()
                        val timeDelta = (location.time - lastLoc.time) / 1000.0
                        val speed = if (timeDelta > 0) distanceDelta / timeDelta else 0.0
                        
                        // Filter noise (Jarak > 1.0 meter, kecepatan < 15 m/s)
                        if (distanceDelta > 1.0 && speed < 15.0) {
                            TrackingManager.addLocationPoint(currentGeoPoint, distanceDelta)
                            previousLocation = location
                        }
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pantau Jompo")
            .setContentText("Sedang melacak aktivitas...")
            //.setSmallIcon(android.R.drawable.ic_menu_mylocation) // Usually use app icon
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        startLocationUpdates()
        startTimer()

        // Update notification occasionally
        serviceScope.launch {
            while (isActive) {
                delay(5000)
                if (TrackingManager.isTracking.value) {
                    val sec = TrackingManager.seconds.value
                    val dist = TrackingManager.totalDistanceMeters.value / 1000.0
                    val timeStr = String.format(Locale.US, "%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, sec % 60)
                    val distStr = String.format(Locale.US, "%.2f km", dist)
                    
                    val updateNotification = NotificationCompat.Builder(this@LocationTrackingService, CHANNEL_ID)
                        .setContentTitle("Sedang Berjalan - $distStr")
                        .setContentText("Waktu: $timeStr")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .build()
                        
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.notify(NOTIFICATION_ID, updateNotification)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setMinUpdateIntervalMillis(1000)
            .build()
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                delay(1000)
                TrackingManager.incrementTime()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Pantau Jompo Tracking Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }
}
