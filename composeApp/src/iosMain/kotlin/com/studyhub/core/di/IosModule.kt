package com.studyhub.core.di

import com.studyhub.data.local.DatabaseDriverFactory
import com.studyhub.core.util.createDataStore
import com.studyhub.core.util.IosNetworkMonitor
import com.studyhub.core.util.NetworkMonitor
import com.studyhub.core.notification.NotificationManager
import com.studyhub.core.notification.IosNotificationManager
import org.koin.dsl.module


val iosModule = module {
    single { DatabaseDriverFactory() }
    single { createDataStore() }
    single<NetworkMonitor> { IosNetworkMonitor() }
    single<NotificationManager> { IosNotificationManager() }
}

/** Helper untuk dipanggil dari Swift code. */
fun initKoinIOS() {
    initKoin(platformModules = listOf(iosModule))
}
