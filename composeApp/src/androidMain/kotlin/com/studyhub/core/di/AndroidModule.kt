package com.studyhub.core.di

import com.studyhub.data.local.DatabaseDriverFactory
import com.studyhub.core.util.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { createDataStore(androidContext()) }
}
