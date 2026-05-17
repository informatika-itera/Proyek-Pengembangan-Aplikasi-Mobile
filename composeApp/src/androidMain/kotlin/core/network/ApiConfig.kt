package core.network

import com.studyhub.BuildConfig

actual object ApiConfig {
    actual val groqApiKey: String = BuildConfig.GROQ_API_KEY
}
