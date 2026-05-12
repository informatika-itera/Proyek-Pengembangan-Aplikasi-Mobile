package com.example.pantaujompo

expect class PlatformLocationProvider() {
    fun requestSingleUpdate(callback: (Double, Double) -> Unit)
}