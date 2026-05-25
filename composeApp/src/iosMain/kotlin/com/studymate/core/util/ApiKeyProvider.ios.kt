package com.studymate.core.util

import platform.Foundation.NSBundle

actual fun getApiKey(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY") as? String ?: ""
}
