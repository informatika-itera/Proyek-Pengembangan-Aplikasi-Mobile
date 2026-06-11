package com.mywallet.di

import com.mywallet.data.local.AndroidDriverFactory
import com.mywallet.data.local.DriverFactory
import com.mywallet.data.local.UserPreferences
import com.mywallet.data.local.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<DriverFactory> { AndroidDriverFactory(androidContext()) }
    single { UserPreferences(createDataStore(androidContext())) }
}
