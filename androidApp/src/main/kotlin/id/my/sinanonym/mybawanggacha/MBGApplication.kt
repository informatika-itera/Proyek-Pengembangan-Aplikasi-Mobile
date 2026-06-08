package id.my.sinanonym.mybawanggacha

import android.app.Application
import id.my.sinanonym.mybawanggacha.core.di.androidModule
import id.my.sinanonym.mybawanggacha.core.di.initKoin
import id.my.sinanonym.mybawanggacha.core.network.ApiConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class
 * * Entry point untuk inisialisasi app-wide dependencies.
 */
class MBGApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ApiConfig.initialize(BuildConfig.GEMINI_API_KEY)

        initKoin(
            platformModules = listOf(androidModule),
            enableNetworkLogging = BuildConfig.DEBUG
        ) {
            androidLogger()
            androidContext(this@MBGApplication)
        }
    }
}