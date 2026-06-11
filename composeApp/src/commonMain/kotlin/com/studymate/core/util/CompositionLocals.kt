package com.studymate.core.util

import androidx.compose.runtime.compositionLocalOf

val LocalGoogleAuth = compositionLocalOf<() -> Unit> { {} }
