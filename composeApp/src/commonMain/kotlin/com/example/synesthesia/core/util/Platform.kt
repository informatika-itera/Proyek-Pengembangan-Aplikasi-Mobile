package com.example.synesthesia.core.util

/**
 * Platform-specific utility functions.
 */

/**
 * Gets the current application version name.
 * 
 * - Android: From BuildConfig.VERSION_NAME
 * - iOS: From NSBundle (CFBundleShortVersionString)
 */
expect fun getAppVersion(): String
