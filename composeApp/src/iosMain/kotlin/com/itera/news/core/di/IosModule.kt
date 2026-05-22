package com.itera.news.core.di

import com.itera.news.core.util.DatabaseDriverFactory
import com.itera.news.data.local.NewsDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { NewsDatabase(DatabaseDriverFactory().createDriver()) }
}