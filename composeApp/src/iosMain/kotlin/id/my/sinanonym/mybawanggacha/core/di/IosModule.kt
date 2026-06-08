package id.my.sinanonym.mybawanggacha.core.di

import id.my.sinanonym.mybawanggacha.core.util.DatabaseDriverFactory
import id.my.sinanonym.mybawanggacha.data.local.datastore.DataStoreFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
}

private var koinStarted = false

fun initKoinIOS() {
    if (koinStarted) return

    initKoin(
        platformModules = listOf(iosModule)
    )

    koinStarted = true
}