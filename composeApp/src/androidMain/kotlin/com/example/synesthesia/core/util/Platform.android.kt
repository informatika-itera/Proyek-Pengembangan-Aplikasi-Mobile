package com.example.synesthesia.core.util

import com.example.synesthesia.BuildConfig

/**
 * Android implementation of getAppVersion.
 */
actual fun getAppVersion(): String = BuildConfig.VERSION_NAME
