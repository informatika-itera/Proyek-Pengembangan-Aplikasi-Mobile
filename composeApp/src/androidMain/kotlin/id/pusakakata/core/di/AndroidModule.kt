package id.pusakakata.core.di

import id.pusakakata.core.util.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
}
