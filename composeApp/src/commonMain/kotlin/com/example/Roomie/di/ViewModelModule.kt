package com.example.Roomie.di

import com.example.Roomie.presentation.home.HomeViewModel
import com.example.Roomie.presentation.map.MapViewModel
import com.example.Roomie.presentation.facility.FacilityDetailViewModel
import com.example.Roomie.presentation.report.ReportViewModel
import com.example.Roomie.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::FacilityDetailViewModel)
    viewModelOf(::ReportViewModel)
    viewModelOf(::ProfileViewModel)
}
