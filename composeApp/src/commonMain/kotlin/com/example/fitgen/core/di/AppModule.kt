package com.example.fitgen.core.di

import com.example.fitgen.core.network.HttpClientFactory
import com.example.fitgen.core.util.DatabaseDriverFactory
import com.example.fitgen.data.local.FitGenDatabase
import com.example.fitgen.data.local.datastore.DataStoreFactory
import com.example.fitgen.data.local.datastore.UserPreferences
import com.example.fitgen.data.local.datastore.create
import com.example.fitgen.data.remote.api.GeminiService
import com.example.fitgen.data.repository.AIRepositoryImpl
// Sprint 2: import com.example.fitgen.data.repository.WorkoutRepositoryImpl
// Sprint 2: import com.example.fitgen.data.repository.MealRepositoryImpl
// Sprint 2: import com.example.fitgen.data.repository.GpsRepositoryImpl
// Sprint 3: import com.example.fitgen.data.repository.BodyMetricRepositoryImpl
import com.example.fitgen.domain.repository.AIRepository
// Sprint 2: import com.example.fitgen.domain.repository.WorkoutRepository
// Sprint 2: import com.example.fitgen.domain.repository.MealRepository
// Sprint 2: import com.example.fitgen.domain.repository.GpsRepository
// Sprint 3: import com.example.fitgen.domain.repository.BodyMetricRepository
// Sprint 2: import com.example.fitgen.domain.usecase.workout.*
// Sprint 2: import com.example.fitgen.domain.usecase.meal.*
// Sprint 2: import com.example.fitgen.domain.usecase.gps.*
// Sprint 3: import com.example.fitgen.domain.usecase.bodymetric.*
// Sprint 2: import com.example.fitgen.presentation.screens.workout.WorkoutViewModel
// Sprint 2: import com.example.fitgen.presentation.screens.meal.MealViewModel
// Sprint 2: import com.example.fitgen.presentation.screens.gps.GpsViewModel
// Sprint 2: import com.example.fitgen.presentation.screens.ai.AIViewModel
// Sprint 3: import com.example.fitgen.presentation.screens.bodymetric.BodyMetricViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================
// Menyediakan HttpClient (Ktor) dan GeminiService untuk fitur AI.

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}

// ==================== DATABASE MODULE ====================
// Menyediakan FitGenDatabase (SQLDelight) untuk semua fitur offline.
// DatabaseDriverFactory disediakan per-platform (androidMain / iosMain).

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        FitGenDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================
// Menyediakan DataStore untuk menyimpan preferensi user (unit sistem, profil, dll).

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================
// Semua repository diikat ke interface-nya menggunakan singleOf(...) bind Interface::class
// agar bisa di-swap saat testing (dependency inversion principle).

val repositoryModule = module {

    // --- AI Repository (Sprint 1) ---
    singleOf(::AIRepositoryImpl) bind AIRepository::class

    // --- Workout Repository (Sprint 2) ---
    // singleOf(::WorkoutRepositoryImpl) bind WorkoutRepository::class

    // --- Meal / Nutrition Repository (Sprint 2) ---
    // singleOf(::MealRepositoryImpl) bind MealRepository::class

    // --- GPS Activity Repository (Sprint 2) ---
    // singleOf(::GpsRepositoryImpl) bind GpsRepository::class

    // --- Body Metric Repository (Sprint 3) ---
    // singleOf(::BodyMetricRepositoryImpl) bind BodyMetricRepository::class
}

// ==================== USE CASE MODULE ====================
// Setiap Use Case mewakili satu aksi bisnis spesifik (Single Responsibility).

val useCaseModule = module {

    // --- AI Use Cases (Sprint 1) ---
    // singleOf(::GenerateWorkoutPlanUseCase)
    // singleOf(::AnalyzeNutritionUseCase)

    // --- Workout Use Cases (Sprint 2) ---
    // singleOf(::GetAllWorkoutsUseCase)
    // singleOf(::SaveWorkoutUseCase)
    // singleOf(::DeleteWorkoutUseCase)
    // singleOf(::GetWorkoutByIdUseCase)

    // --- Meal / Nutrition Use Cases (Sprint 2) ---
    // singleOf(::GetAllMealsUseCase)
    // singleOf(::LogMealUseCase)
    // singleOf(::DeleteMealUseCase)
    // singleOf(::GetDailyNutritionSummaryUseCase)

    // --- GPS Activity Use Cases (Sprint 2) ---
    // singleOf(::StartGpsTrackingUseCase)
    // singleOf(::StopGpsTrackingUseCase)
    // singleOf(::GetActivityHistoryUseCase)

    // --- Body Metric Use Cases (Sprint 3) ---
    // singleOf(::SaveBodyMetricUseCase)
    // singleOf(::GetBodyMetricHistoryUseCase)
    // singleOf(::GetLatestBodyMetricUseCase)
}

// ==================== VIEWMODEL MODULE ====================
// ViewModel di-inject menggunakan viewModelOf() agar lifecycle-aware.

val viewModelModule = module {

    // --- AI ViewModel (Sprint 1) ---
    // viewModelOf(::AIViewModel)

    // --- Workout ViewModel (Sprint 2) ---
    // viewModelOf(::WorkoutViewModel)

    // --- Meal ViewModel (Sprint 2) ---
    // viewModelOf(::MealViewModel)

    // --- GPS ViewModel (Sprint 2) ---
    // viewModelOf(::GpsViewModel)

    // --- Body Metric ViewModel (Sprint 3) ---
    // viewModelOf(::BodyMetricViewModel)
}

// ==================== SHARED MODULES ====================
// Daftar semua modul yang digunakan bersama oleh Android dan iOS.
// Platform-specific modules (mis. DatabaseDriverFactory) didaftarkan
// terpisah di masing-masing platform lewat parameter `platformModules`.

val sharedModules: List<Module> = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

// ==================== INIT FUNCTION ====================
// Dipanggil dari entry point platform (MainActivity / iOSApp).
// platformModules berisi binding platform-specific seperti DatabaseDriverFactory.

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
