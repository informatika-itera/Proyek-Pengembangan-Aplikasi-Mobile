package id.my.sinanonym.mybawanggacha.core.di

import id.my.sinanonym.mybawanggacha.core.util.DatabaseDriverFactory
import id.my.sinanonym.mybawanggacha.data.local.NoteDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        NoteDatabase(driverFactory.createDriver())
    }
}
