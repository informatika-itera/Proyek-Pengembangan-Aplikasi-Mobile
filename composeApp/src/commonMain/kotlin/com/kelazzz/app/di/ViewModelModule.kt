package com.kelazzz.app.di

import com.kelazzz.app.presentation.screens.login.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * ViewModel Layer — Koin DI Module
 * 
 * Menyediakan ViewModels untuk presentation layer.
 */
val viewModelModule = module {
    // ==================== Sprint 2 ====================
    viewModelOf(::LoginViewModel)
    
    // TODO: Sprint 2 — more ViewModels
    // viewModelOf(::HomeViewModel)
    
    // TODO: Sprint 3
    // viewModelOf(::PresensiViewModel)
    // viewModelOf(::DaftarPresensiViewModel)
    
    // TODO: Sprint 4
    // viewModelOf(::KalenderViewModel)
    // viewModelOf(::AIAsistenViewModel)
}
