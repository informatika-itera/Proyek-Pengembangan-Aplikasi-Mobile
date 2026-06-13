package com.example.sholatyuk.core.di

import com.example.sholatyuk.android.scheduler.AdzanSchedulerImpl
import com.example.sholatyuk.android.scheduler.CompassSensor
import com.example.sholatyuk.core.location.LocationService
import com.example.sholatyuk.core.sensor.ICompassSensor
import com.example.sholatyuk.core.util.DatabaseDriverFactory
import com.example.sholatyuk.data.local.datastore.DataStoreFactory
import com.example.sholatyuk.domain.scheduler.AdzanScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
    single { LocationService() }
    single<AdzanScheduler>  { AdzanSchedulerImpl(androidContext()) }
    single<ICompassSensor>  { CompassSensor(androidContext()) }  // ← binding interface → impl
}