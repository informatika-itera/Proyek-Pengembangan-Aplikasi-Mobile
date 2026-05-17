package com.example.tabungin.core.di

import com.example.tabungin.core.network.HttpClientFactory
import com.example.tabungin.core.util.DatabaseDriverFactory
import com.example.tabungin.data.local.TabunginDatabase
import com.example.tabungin.data.local.datastore.DataStoreFactory
import com.example.tabungin.data.local.datastore.UserPreferences
import com.example.tabungin.data.local.datastore.create
import com.example.tabungin.data.remote.api.GeminiService
import com.example.tabungin.data.repository.TargetRepositoryImpl
import com.example.tabungin.domain.repository.TargetRepository
import com.example.tabungin.domain.usecase.*
import com.example.tabungin.presentation.screens.add_edit.AddEditViewModel
import com.example.tabungin.presentation.screens.detail.DetailViewModel
import com.example.tabungin.presentation.screens.home.HomeViewModel
import com.example.tabungin.presentation.screens.riwayat.RiwayatViewModel
import com.example.tabungin.presentation.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module



val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}



val databaseModule = module {
    single { get<DatabaseDriverFactory>().createDriver() }
    single { TabunginDatabase(get()) }
}



val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}



val repositoryModule = module {
    single<TargetRepository> { TargetRepositoryImpl(get()) }
}



val useCaseModule = module {
    factory { GetAllTargetsUseCase(get()) }
    factory { GetTargetByIdUseCase(get()) }
    factory { InsertTargetUseCase(get()) }
    factory { UpdateTargetUseCase(get()) }
    factory { DeleteTargetUseCase(get()) }
    factory { GetSetoranByTargetUseCase(get()) }
    factory { GetAllSetoranUseCase(get()) }
    factory { InsertSetoranUseCase(get()) }
    factory { DeleteSetoranUseCase(get()) }
}



val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { (id: Long) -> DetailViewModel(id, get(), get(), get(), get()) }
    viewModel { params -> AddEditViewModel(params.getOrNull<Long>(), get(), get(), get()) }
    viewModel { RiwayatViewModel(get()) }
    viewModel { SettingsViewModel() }
}



val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)



fun commonModules(): List<Module> = listOf(
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
