package com.mywallet.di

import com.mywallet.data.local.SavingsGoalLocalDataSource
import com.mywallet.data.local.TransactionLocalDataSource
import com.mywallet.data.repository.SavingsGoalRepositoryImpl
import com.mywallet.data.repository.TransactionRepositoryImpl
import com.mywallet.data.repository.UserRepositoryImpl
import com.mywallet.data.remote.CurrencyService
import com.mywallet.data.remote.CurrencyServiceImpl
import com.mywallet.data.remote.createHttpClient
import com.mywallet.domain.repository.SavingsGoalRepository
import com.mywallet.domain.repository.TransactionRepository
import com.mywallet.domain.repository.UserRepository
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val dataModule = module {
    single { get<com.mywallet.data.local.DriverFactory>().createDriver() }
    single { TransactionLocalDataSource(get()) }
    single { SavingsGoalLocalDataSource(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<SavingsGoalRepository> { SavingsGoalRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl() }
    
    single { createHttpClient() }
    single<CurrencyService> { CurrencyServiceImpl(get()) }
}
