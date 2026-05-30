package com.kosthub.app.platform

import com.kosthub.app.BuildConfig

actual object PlatformConfig {
    actual val geminiApiKey: String get() = BuildConfig.GEMINI_API_KEY
}
