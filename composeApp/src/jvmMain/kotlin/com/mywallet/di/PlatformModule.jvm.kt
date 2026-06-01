package com.mywallet.di

import com.mywallet.data.local.DriverFactory
import com.mywallet.data.local.JvmDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<DriverFactory> { JvmDriverFactory() }
}
