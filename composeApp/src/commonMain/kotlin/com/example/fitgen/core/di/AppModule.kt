package com.example.fitgen.core.di

import com.example.fitgen.core.network.HttpClientFactory
import com.example.fitgen.core.util.DatabaseDriverFactory
import com.example.fitgen.data.local.FitGenDatabase
import com.example.fitgen.data.local.datastore.DataStoreFactory
import com.example.fitgen.data.local.datastore.UserPreferences
import com.example.fitgen.data.local.datastore.create
import com.example.fitgen.data.remote.api.ExerciseApiService
import com.example.fitgen.data.remote.api.GeminiService
import com.example.fitgen.data.remote.api.GroqService
import com.example.fitgen.data.repository.AIRepositoryImpl
import com.example.fitgen.data.repository.BodyMetricRepositoryImpl
import com.example.fitgen.data.repository.CustomRoutineRepositoryImpl
import com.example.fitgen.data.repository.ExerciseImageRepositoryImpl
import com.example.fitgen.data.repository.MealRepositoryImpl
import com.example.fitgen.data.repository.WorkoutRepositoryImpl
import com.example.fitgen.domain.repository.AIRepository
import com.example.fitgen.domain.repository.BodyMetricRepository
import com.example.fitgen.domain.repository.CustomRoutineRepository
import com.example.fitgen.domain.repository.ExerciseImageRepository
import com.example.fitgen.domain.repository.MealRepository
import com.example.fitgen.domain.repository.WorkoutRepository
import com.example.fitgen.domain.usecase.GetAllWorkoutsUseCase
import com.example.fitgen.domain.usecase.GetDailyCaloriesUseCase
import com.example.fitgen.domain.usecase.GetDailyWaterGlassesUseCase
import com.example.fitgen.domain.usecase.GetExerciseGifUseCase
import com.example.fitgen.domain.usecase.GetLoginStreakUseCase
import com.example.fitgen.domain.usecase.LogMealUseCase
import com.example.fitgen.domain.usecase.LogWorkoutUseCase
import com.example.fitgen.domain.usecase.UpdateLoginStreakUseCase
import com.example.fitgen.domain.usecase.AddWaterGlassUseCase
import com.example.fitgen.domain.usecase.RemoveWaterGlassUseCase
import com.example.fitgen.domain.usecase.AnalyzeFoodNutritionUseCase
import com.example.fitgen.presentation.screens.home.HomeDashboardViewModel
import com.example.fitgen.presentation.screens.home.HomeViewModel
import com.example.fitgen.presentation.screens.nutrition.AddMealViewModel
import com.example.fitgen.presentation.screens.nutrition.NutritionViewModel
import com.example.fitgen.presentation.screens.profile.ProfileViewModel
import com.example.fitgen.presentation.screens.workout.AddWorkoutViewModel
import com.example.fitgen.presentation.screens.workout.ExerciseDetailViewModel
import com.example.fitgen.presentation.screens.workout.ActiveSessionViewModel
import com.example.fitgen.presentation.screens.workout.WorkoutListViewModel
import com.example.fitgen.presentation.screens.splash.SplashViewModel
import com.example.fitgen.presentation.screens.onboarding.OnboardingViewModel
import com.example.fitgen.presentation.screens.ai.AIAssistantViewModel
import com.example.fitgen.presentation.screens.ai.DynamicWorkoutViewModel
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================
val networkModule = module {
    // Berikan tipe spesifik <HttpClient> agar dependency terdeteksi
    single<HttpClient> { HttpClientFactory.create(enableLogging = true) }

    // Inject HttpClient secara eksplisit ke GeminiService
    single { GeminiService(get<HttpClient>()) }
    singleOf(::GroqService)
    singleOf(::ExerciseApiService)
}

// ==================== DATABASE MODULE ====================
val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        FitGenDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================
val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================
val repositoryModule = module {
    // Jika singleOf melempar error "None of the following candidates...",
    // gunakan inisialisasi manual seperti ini agar Koin tidak bingung
    single<AIRepository> { AIRepositoryImpl(get(), get()) } // Sesuaikan jumlah get() dengan parameter di AIRepositoryImpl

    singleOf(::WorkoutRepositoryImpl) bind WorkoutRepository::class
    single<MealRepository> { MealRepositoryImpl() }
    single<BodyMetricRepository> { BodyMetricRepositoryImpl() }
    singleOf(::ExerciseImageRepositoryImpl) bind ExerciseImageRepository::class
    single<CustomRoutineRepository> { CustomRoutineRepositoryImpl(get()) }
}

// ==================== USE CASE MODULE ====================
val useCaseModule = module {
    singleOf(::GetExerciseGifUseCase)

    // Berikan tipe eksplisit pada UseCase analisis makanan agar get() terarah
    single { AnalyzeFoodNutritionUseCase(get<AIRepository>()) }

    singleOf(::GetAllWorkoutsUseCase)
    singleOf(::LogWorkoutUseCase)

    singleOf(::LogMealUseCase)
    singleOf(::GetDailyCaloriesUseCase)

    factory { UpdateLoginStreakUseCase(get()) }
    factory { GetLoginStreakUseCase(get()) }
    factory { AddWaterGlassUseCase(get()) }
    factory { RemoveWaterGlassUseCase(get()) }
    factory { GetDailyWaterGlassesUseCase(get()) }
}

// ==================== VIEWMODEL MODULE ====================
val viewModelModule = module {
    viewModelOf(::AIAssistantViewModel)
    factory { DynamicWorkoutViewModel(get(), get(), get()) }
    viewModelOf(::HomeViewModel)
    viewModelOf(::HomeDashboardViewModel)
    viewModelOf(::WorkoutListViewModel)
    viewModelOf(::AddWorkoutViewModel)
    viewModelOf(::ExerciseDetailViewModel)
    viewModelOf(::ActiveSessionViewModel)
    viewModelOf(::NutritionViewModel)
    viewModelOf(::AddMealViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::OnboardingViewModel)
    factory { params -> com.example.fitgen.presentation.screens.challenge.ChallengeDetailViewModel(params.get(), get(), get()) }
    factory { params -> com.example.fitgen.presentation.screens.challenge.ChallengeDayViewModel(params.get(0), params.get(1), get()) }
}

// ==================== SHARED MODULES ====================
val sharedModules: List<Module> = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

// ==================== INIT FUNCTION ====================
fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}