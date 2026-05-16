package com.itera.news.di

import androidx.room.Room
import com.itera.news.data.local.NewsDatabase
import com.itera.news.data.remote.NewsApi
import com.itera.news.data.remote.GeminiService
import com.itera.news.data.repository.NewsRepositoryImpl
import com.itera.news.domain.repository.NewsRepository
import com.itera.news.domain.usecase.GetMbgNewsUseCase
import com.itera.news.presentation.viewmodel.NewsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 1. Tambahkan modul untuk Room Database
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            NewsDatabase::class.java,
            "news_db"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<NewsDatabase>().articleDao }
}

val networkModule = module {
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }
    
    single { GeminiService("AIzaSyDLHNG88NgO55_vSkKYpLByYsxdcBls9xs") }
}

val repositoryModule = module {
    // 2. Update dengan menambahkan get() ketiga untuk parameter GeminiService
    single<NewsRepository> { NewsRepositoryImpl(get(), get(), get()) }
}

val useCaseModule = module {
    factory { GetMbgNewsUseCase(get()) }
}

val viewModelModule = module {
    // Tambahkan get() kedua
    viewModel { NewsViewModel(get(), get()) } 
}