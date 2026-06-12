package com.example.bridgebit

import android.app.Application
import com.example.bridgebit.core.di.androidModule
import com.example.bridgebit.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class
 * 
 * Entry point untuk inisialisasi app-wide dependencies.
 */
class NoteAIApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Register Activity Lifecycle Callbacks to track current Activity
        registerActivityLifecycleCallbacks(object : android.app.Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: android.app.Activity, savedInstanceState: android.os.Bundle?) {}
            override fun onActivityStarted(activity: android.app.Activity) {}
            override fun onActivityResumed(activity: android.app.Activity) { currentActivity = activity }
            override fun onActivityPaused(activity: android.app.Activity) { if (currentActivity == activity) currentActivity = null }
            override fun onActivityStopped(activity: android.app.Activity) {}
            override fun onActivitySaveInstanceState(activity: android.app.Activity, outState: android.os.Bundle) {}
            override fun onActivityDestroyed(activity: android.app.Activity) {}
        })

        // Initialize Koin DI
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@NoteAIApplication)
        }
    }

    companion object {
        /** Singleton instance untuk akses ApplicationContext di luar Composable scope. */
        lateinit var instance: NoteAIApplication
            private set
            
        var currentActivity: android.app.Activity? = null
            private set
    }
}
