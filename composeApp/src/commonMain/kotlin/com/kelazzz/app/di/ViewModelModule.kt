package com.kelazzz.app.di

import com.kelazzz.app.presentation.screens.home.HomeViewModel
import com.kelazzz.app.presentation.screens.jadwal.JadwalListViewModel
import com.kelazzz.app.presentation.screens.jadwal.detail.JadwalDetailViewModel
import com.kelazzz.app.presentation.screens.jadwal.addedit.JadwalAddEditViewModel
import com.kelazzz.app.presentation.screens.login.LoginViewModel
import com.kelazzz.app.presentation.screens.profile.ProfileViewModel
import com.kelazzz.app.presentation.screens.rekap.RekapViewModel
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
    viewModelOf(::JadwalListViewModel)
    viewModelOf(::JadwalDetailViewModel)
    viewModelOf(::JadwalAddEditViewModel)
    viewModelOf(::RekapViewModel)
    // TODO: Sprint 2 — more ViewModels
    // viewModelOf(::HomeViewModel)
    
    // TODO: Sprint 3
    // viewModelOf(::PresensiViewModel)
    // viewModelOf(::DaftarPresensiViewModel)
    
    // TODO: Sprint 4
    // viewModelOf(::KalenderViewModel)
    // viewModelOf(::AIAsistenViewModel)
}
