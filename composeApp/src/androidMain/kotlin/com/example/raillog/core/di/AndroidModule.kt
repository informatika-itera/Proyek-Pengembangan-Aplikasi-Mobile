package com.example.raillog.core.di

import com.example.raillog.core.util.CriticalItemsNotifier
import com.example.raillog.core.util.DatabaseDriverFactory
import com.example.raillog.core.util.NotificationServiceImpl
import com.example.raillog.data.local.datastore.DataStoreFactory
import com.example.raillog.domain.repository.NotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * Menyediakan dependencies yang membutuhkan `Context`:
 * - DatabaseDriverFactory : untuk SQLDelight driver
 * - DataStoreFactory      : untuk lokasi file preferences
 * - CriticalItemsNotifier : untuk notifikasi komponen kritis
 * - NotificationService   : implementasi notifikasi untuk Android
 *
 * Lokasi: androidMain/core/di/AndroidModule.kt
 */
val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
    single { NotificationServiceImpl(androidContext()) } bind NotificationService::class
    single { CriticalItemsNotifier(androidContext(), get(), get()) }
}
