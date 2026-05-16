package com.soundletter.app.di

import com.soundletter.app.presentation.screens.home.HomeViewModel
import com.soundletter.app.presentation.screens.search.SearchViewModel
import com.soundletter.app.presentation.screens.compose.ComposeViewModel
import com.soundletter.app.presentation.screens.history.HistoryViewModel
import com.soundletter.app.presentation.screens.detail.DetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { HomeViewModel() }
    viewModel { SearchViewModel() }
    viewModel { ComposeViewModel() }
    viewModel { HistoryViewModel() }
    viewModel { DetailViewModel() }
}
