package id.my.sinanonym.mybawanggacha.core.di

import id.my.sinanonym.mybawanggacha.core.coroutines.AppDispatchers
import org.koin.dsl.module

val dispatchersModule = module {
    single { AppDispatchers() }
}
