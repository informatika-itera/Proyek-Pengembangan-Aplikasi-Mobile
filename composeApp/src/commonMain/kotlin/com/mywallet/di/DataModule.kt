package com.mywallet.di

import com.mywallet.data.local.TransactionLocalDataSource
import com.mywallet.data.repository.TransactionRepositoryImpl
import com.mywallet.domain.repository.TransactionRepository
import org.koin.dsl.module

val dataModule = module {
    single { TransactionLocalDataSource() }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
}