package com.example.bridgebit.core.notification

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.bridgebit.NoteAIApplication

actual class NotificationService actual constructor() {

    private val context: Context
        get() = NoteAIApplication.instance

    private val channelId = "BridgeBitChannel"

    init {
        createNotificationChannel()
    }

    actual fun requestPermission(onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                onResult(true)
            } else {
                val activity = NoteAIApplication.currentActivity
                if (activity != null) {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
                    
                    val app = activity.application
                    var callback: android.app.Application.ActivityLifecycleCallbacks? = null
                    callback = object : android.app.Application.ActivityLifecycleCallbacks {
                        override fun onActivityCreated(a: Activity, savedInstanceState: android.os.Bundle?) {}
                        override fun onActivityStarted(a: Activity) {}
                        override fun onActivityResumed(a: Activity) {
                            if (a == activity) {
                                callback?.let { app.unregisterActivityLifecycleCallbacks(it) }
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    onResult(true)
                                } else {
                                    onResult(false)
                                }
                            }
                        }
                        override fun onActivityPaused(a: Activity) {}
                        override fun onActivityStopped(a: Activity) {}
                        override fun onActivitySaveInstanceState(a: Activity, outState: android.os.Bundle) {}
                        override fun onActivityDestroyed(a: Activity) {
                            if (a == activity) callback?.let { app.unregisterActivityLifecycleCallbacks(it) }
                        }
                    }
                    app.registerActivityLifecycleCallbacks(callback)
                } else {
                    onResult(false)
                }
            }
        } else {
            onResult(true)
        }
    }

    actual fun showNotification(title: String, body: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && 
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val notificationId = System.currentTimeMillis().toInt()
        
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Fallback icon, will use our app icon if set
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "BridgeBit Notifications"
            val descriptionText = "Channel untuk notifikasi pengingat BridgeBit"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
