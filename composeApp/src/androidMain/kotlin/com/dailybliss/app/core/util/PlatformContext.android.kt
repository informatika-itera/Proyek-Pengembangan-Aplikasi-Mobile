package com.dailybliss.app.core.util

/**
 * Android implementation of PlatformContext
 * 
 * Kita menggunakan typealias ke android.content.Context agar
 * PlatformContext di Android adalah Context yang sebenarnya.
 */
actual typealias PlatformContext = android.content.Context
