package com.studyhub.core.network

import com.studyhub.BuildConfig

actual object ApiConfig {
    actual val groqApiKey: String = BuildConfig.GROQ_API_KEY
    actual val groqBaseUrl: String = "https://api.groq.com/openai/v1"
    actual val groqModel: String = "llama3-8b-8192"
}
