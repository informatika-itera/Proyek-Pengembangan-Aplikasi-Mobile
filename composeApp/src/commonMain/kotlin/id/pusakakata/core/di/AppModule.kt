package id.pusakakata.core.di

import id.pusakakata.core.network.ApiConfig
import id.pusakakata.di.allPusakaKataModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

val sharedModules = allPusakaKataModules

fun initKoin(
    geminiApiKey: String = "",
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    if (geminiApiKey.isNotBlank()) {
        ApiConfig.GEMINI_API_KEY = geminiApiKey
    }

    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
