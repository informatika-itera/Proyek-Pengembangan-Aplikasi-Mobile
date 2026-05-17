package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.core.util.DatabaseDriverFactory
import com.example.mybawanggacha.data.local.NoteDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        NoteDatabase(driverFactory.createDriver())
    }
}
