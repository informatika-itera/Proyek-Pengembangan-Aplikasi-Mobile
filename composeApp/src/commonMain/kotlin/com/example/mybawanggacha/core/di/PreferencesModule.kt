package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.data.local.datastore.DataStoreFactory
import com.example.mybawanggacha.data.local.datastore.UserPreferences
import com.example.mybawanggacha.data.local.datastore.create
import org.koin.dsl.module

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}
