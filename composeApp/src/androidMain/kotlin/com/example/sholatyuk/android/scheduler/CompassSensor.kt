package com.example.sholatyuk.android.scheduler

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.sholatyuk.core.sensor.ICompassSensor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Implementasi Android dari ICompassSensor.
 * Letakkan di androidMain/.../android.scheduler/ (sejajar AdzanSchedulerImpl)
 */
class CompassSensor(private val context: Context) : ICompassSensor {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    override val azimuthFlow: Flow<Float> = callbackFlow {
        val accelerometerReading = FloatArray(3)
        val magnetometerReading  = FloatArray(3)
        val rotationMatrix       = FloatArray(9)
        val orientationAngles    = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER ->
                        System.arraycopy(event.values, 0, accelerometerReading, 0, 3)
                    Sensor.TYPE_MAGNETIC_FIELD ->
                        System.arraycopy(event.values, 0, magnetometerReading, 0, 3)
                }
                val success = SensorManager.getRotationMatrix(
                    rotationMatrix, null,
                    accelerometerReading, magnetometerReading
                )
                if (success) {
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    trySend((azimuth + 360f) % 360f)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(
            listener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
        sensorManager.registerListener(
            listener,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_UI
        )

        awaitClose { sensorManager.unregisterListener(listener) }
    }

    override fun isCompassAvailable(): Boolean =
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null &&
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
}