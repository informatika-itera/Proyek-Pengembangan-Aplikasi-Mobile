package com.studyhub.core.di

import com.studyhub.data.local.DatabaseDriverFactory
import com.studyhub.core.util.createDataStore
import org.koin.dsl.module


val iosModule = module {
    single { DatabaseDriverFactory() }
    single { createDataStore() }
}

/** Helper untuk dipanggil dari Swift code. */
fun initKoinIOS() {
    initKoin(platformModules = listOf(iosModule))
}
