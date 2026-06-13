package com.example.sholatyuk.core.sensor

import kotlinx.coroutines.flow.Flow

/**
 * Interface platform-agnostic untuk sensor kompas.
 * Didefinisikan di commonMain agar QiblaViewModel tidak perlu import Android.
 * Implementasinya (CompassSensor) ada di androidMain.
 */
interface ICompassSensor {
    val azimuthFlow: Flow<Float>
    fun isCompassAvailable(): Boolean
}