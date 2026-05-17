package id.pusakakata

import android.app.Application
import id.pusakakata.core.di.initKoin
import id.pusakakata.core.di.androidModule
import org.koin.android.ext.koin.androidContext

class PusakaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            platformModules = listOf(androidModule),
            config = {
                androidContext(this@PusakaApplication)
            }
        )
    }
}
