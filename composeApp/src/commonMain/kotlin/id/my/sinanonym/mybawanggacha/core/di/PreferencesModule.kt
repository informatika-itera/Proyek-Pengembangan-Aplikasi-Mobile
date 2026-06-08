package id.my.sinanonym.mybawanggacha.core.di

import id.my.sinanonym.mybawanggacha.data.local.datastore.DataStoreFactory
import id.my.sinanonym.mybawanggacha.data.local.datastore.UserPreferences
import id.my.sinanonym.mybawanggacha.data.local.datastore.create
import org.koin.dsl.module

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}
