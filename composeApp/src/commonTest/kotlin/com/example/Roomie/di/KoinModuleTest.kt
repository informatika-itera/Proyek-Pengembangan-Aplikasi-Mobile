package com.example.Roomie.di

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import kotlin.test.Test

@OptIn(KoinExperimentalAPI::class)
class KoinModuleTest : KoinTest {

    @Test
    fun checkAllModules() {
        // Verifikasi apakah semua definisi module Koin valid
        // Catatan: Di KMP, kita mungkin perlu mock beberapa platform-specific dependencies
        // jika checkModules() mencoba menginstansiasi mereka.
        checkModules {
            modules(dataModule, domainModule, viewModelModule)
        }
    }
}
