package com.example.pantaujompo.core.location

import android.content.Context
import android.content.Intent

actual object LocationServiceController {
    actual fun start(context: Any?) {
        val androidContext = context as? Context ?: return
        val intent = Intent(androidContext, LocationTrackingService::class.java)
        androidContext.startService(intent)
    }

    actual fun stop(context: Any?) {
        val androidContext = context as? Context ?: return
        val intent = Intent(androidContext, LocationTrackingService::class.java)
        androidContext.stopService(intent)
    }
}
