package com.example.sholatyuk.core.di

import com.example.sholatyuk.presentation.screens.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val sharedModules = module {
    viewModelOf(::HomeViewModel)
}

fun initKoin(
    platformModules: List<org.koin.core.module.Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
