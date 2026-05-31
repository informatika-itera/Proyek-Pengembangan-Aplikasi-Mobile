package com.example.noteai

import androidx.compose.runtime.Composable
import com.example.noteai.presentation.navigation.AppNavHost
import com.example.noteai.presentation.theme.NoteAITheme
import com.movein.di.networkModule
import com.movein.data.repository.ActivityRepositoryImpl
import com.movein.domain.repository.ActivityRepository
import org.koin.compose.KoinContext
import org.koin.dsl.module

// Modul Koin tambahan untuk menampung data layer baru buatan Raisya
val sprint3DataModule = module {
    // Mendaftarkan ActivityRepository agar siap di-inject ke ViewModel nanti
    single<ActivityRepository> { ActivityRepositoryImpl(get()) }
}

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}