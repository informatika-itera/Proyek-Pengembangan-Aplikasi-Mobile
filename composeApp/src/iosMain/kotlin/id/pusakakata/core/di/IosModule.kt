package id.pusakakata.core.di

import id.pusakakata.core.util.DataStoreFactory
import id.pusakakata.core.util.DatabaseDriverFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
}
