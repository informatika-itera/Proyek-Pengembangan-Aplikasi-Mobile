package com.example.Roomie.di

import com.example.Roomie.domain.usecase.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    // Auth UseCases
    factoryOf(::GetCurrentUserUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::LogoutUseCase)
    
    // Facility UseCases
    factoryOf(::GetBuildingsUseCase)
    factoryOf(::GetRoomsByBuildingUseCase)
    factoryOf(::SearchRoomsUseCase)
    factoryOf(::GetRoomByIdUseCase)
    factoryOf(::UpdateRoomStatusUseCase)
    
    // Report UseCases
    factoryOf(::GetAllReportsUseCase)
    factoryOf(::UpdateReportStatusUseCase)
    factoryOf(::SubmitReportUseCase)

    // Booking UseCases
    factoryOf(::GetAllBookingsUseCase)

    // Announcement UseCases
    factoryOf(::GetAllAnnouncementsUseCase)
    factoryOf(::PostAnnouncementUseCase)
}
