package com.studymate.core.util

import com.studymate.BuildConfig

actual fun getApiKey(): String = BuildConfig.GROQ_API_KEY
