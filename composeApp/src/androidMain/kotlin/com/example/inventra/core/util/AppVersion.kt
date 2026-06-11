package com.example.inventra.core.util

import com.example.inventra.BuildConfig

object AppVersion {
    val name: String = BuildConfig.VERSION_NAME
    val code: Int = BuildConfig.VERSION_CODE
    val displayString: String = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
}
