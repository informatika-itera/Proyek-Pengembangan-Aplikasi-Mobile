package com.example.pantaujompo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.example.pantaujompo.core.di.initKoin
import com.example.pantaujompo.data.local.room.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.getOrNull
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. KITA BIKIN MODUL ROOM KHUSUS ANDROID
        val androidModule = module {
            single {
                Room.databaseBuilder(
                    applicationContext, // 🔥 Pakai applicationContext biar aman dari memory leak
                    AppDatabase::class.java,
                    "db_pantau_jompo"
                ).fallbackToDestructiveMigration().build()
            }

            // Sediain DAO-nya buat ditangkep sama ViewModel
            single { get<AppDatabase>().riwayatDao() }

            // 🔥 INI OBATNYA BRAY! Tambahin makananDao di sini 🔥
            single { get<AppDatabase>().makananDao() }
        }

        // 2. LOGIKA SUNTIK KOIN YANG BENAR!
        if (getOrNull() == null) {
            // Kalau Koin belum nyala sama sekali, nyalain dan masukin modulnya
            initKoin(
                platformModules = listOf(androidModule),
                config = {
                    androidContext(this@MainActivity.applicationContext)
                }
            )
        } else {
            // Kalau Koin udah nyala diem-diem di tempat lain, KITA SUNTIK PAKSA modul Android-nya!
            loadKoinModules(androidModule)
        }

        setContent {
            // Panggil UI Utama Aplikasi Lo
            App()
        }
    }
}