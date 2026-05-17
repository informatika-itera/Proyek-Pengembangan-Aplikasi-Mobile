package id.pusakakata.core.di

import id.pusakakata.di.allPusakaKataModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

val sharedModules = allPusakaKataModules

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
