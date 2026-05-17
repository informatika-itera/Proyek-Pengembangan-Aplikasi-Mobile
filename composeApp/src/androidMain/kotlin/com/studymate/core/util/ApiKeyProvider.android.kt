package com.studymate.core.util

import com.studymate.BuildConfig

actual fun getApiKey(): String = BuildConfig.GEMINI_API_KEY
