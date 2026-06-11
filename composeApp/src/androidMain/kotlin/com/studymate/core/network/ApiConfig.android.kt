package com.studymate.core.network

import com.studymate.BuildConfig

actual object ApiConfig {
    actual val groqApiKey: String = BuildConfig.GROQ_API_KEY
    actual val googleWebClientId: String = BuildConfig.GOOGLE_WEB_CLIENT_ID
}
