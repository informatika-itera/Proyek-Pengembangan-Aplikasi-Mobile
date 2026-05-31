package com.kelazzz.app.di

import com.kelazzz.app.presentation.screens.ai.AIViewModel
import com.kelazzz.app.presentation.screens.home.HomeViewModel
import com.kelazzz.app.presentation.screens.jadwal.JadwalListViewModel
import com.kelazzz.app.presentation.screens.jadwal.detail.JadwalDetailViewModel
import com.kelazzz.app.presentation.screens.jadwal.addedit.JadwalAddEditViewModel
import com.kelazzz.app.presentation.screens.login.LoginViewModel
import com.kelazzz.app.presentation.screens.profile.ProfileViewModel
import com.kelazzz.app.presentation.screens.rekap.RekapViewModel
import com.kelazzz.app.presentation.screens.presensi.PresensiViewModel
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
    
    // ==================== Sprint 3 ====================
    viewModelOf(::PresensiViewModel)
    
    // ==================== Sprint 4 — AI ====================
    viewModelOf(::AIViewModel)
}

