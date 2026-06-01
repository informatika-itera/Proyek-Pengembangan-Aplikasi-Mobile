package com.example.tabungin.core.di

import android.content.Context
import android.content.SharedPreferences
import com.example.tabungin.core.util.DatabaseDriverFactory
import com.example.tabungin.data.local.datastore.DataStoreFactory
import com.example.tabungin.notification.AlarmScheduler
import com.example.tabungin.notification.NotificationService
import com.example.tabungin.notification.NotificationServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
    single { AlarmScheduler(androidContext()) }
    single<NotificationService> { NotificationServiceImpl(androidContext(), get()) }
    single<SharedPreferences> {
        androidContext().getSharedPreferences("tabungin_notif_prefs", Context.MODE_PRIVATE)
    }
}
