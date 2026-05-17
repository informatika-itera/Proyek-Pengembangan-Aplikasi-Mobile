package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import org.koin.dsl.module

val dispatchersModule = module {
    single { AppDispatchers() }
}
