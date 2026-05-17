package com.mywallet.di

import com.mywallet.presentation.add.AddTransactionViewModel
import com.mywallet.presentation.detail.DetailViewModel
import com.mywallet.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::AddTransactionViewModel)
}