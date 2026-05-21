package com.mywallet.di

import com.mywallet.presentation.screens.add.AddTransactionViewModel
import com.mywallet.presentation.screens.detail.DetailViewModel
import com.mywallet.presentation.screens.home.HomeViewModel
import com.mywallet.presentation.screens.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::AddTransactionViewModel)
    viewModelOf(::ProfileViewModel)
}
