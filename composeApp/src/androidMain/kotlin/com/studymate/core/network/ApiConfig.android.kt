package com.studymate.core.network

import com.studymate.BuildConfig

actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
    actual val googleWebClientId: String = BuildConfig.GOOGLE_WEB_CLIENT_ID
}
