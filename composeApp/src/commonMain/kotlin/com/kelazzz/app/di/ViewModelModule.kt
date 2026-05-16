package com.kelazzz.app.di

import com.kelazzz.app.presentation.screens.home.HomeViewModel
import com.kelazzz.app.presentation.screens.login.LoginViewModel
import com.kelazzz.app.presentation.screens.profile.ProfileViewModel
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
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
    // TODO: Sprint 2 — more ViewModels
    // viewModelOf(::HomeViewModel)
    
    // TODO: Sprint 3
    // viewModelOf(::PresensiViewModel)
    // viewModelOf(::DaftarPresensiViewModel)
    
    // TODO: Sprint 4
    // viewModelOf(::KalenderViewModel)
    // viewModelOf(::AIAsistenViewModel)
}
