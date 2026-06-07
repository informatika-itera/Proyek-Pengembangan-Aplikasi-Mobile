package com.studyhub.core.di

import com.studyhub.data.local.DatabaseDriverFactory
import com.studyhub.core.util.createDataStore
import com.studyhub.core.util.AndroidNetworkMonitor
import com.studyhub.core.util.NetworkMonitor
import com.studyhub.core.notification.NotificationManager
import com.studyhub.core.notification.AndroidNotificationManager
import com.studyhub.domain.repository.ReminderRepository
import com.studyhub.data.repository.ReminderRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { createDataStore(androidContext()) }
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }
    single<NotificationManager> { AndroidNotificationManager(androidContext()) }
    single<ReminderRepository> {
        ReminderRepositoryImpl(androidContext(), get(), get())
    }
}
