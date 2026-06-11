package com.example.inventra.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalStrings = staticCompositionLocalOf<Strings> { IndonesianStrings }

@Composable
fun ProvideStrings(
    language: Language,
    content: @Composable () -> Unit
) {
    val strings = when (language) {
        Language.INDONESIAN -> IndonesianStrings
        Language.ENGLISH -> EnglishStrings
    }
    CompositionLocalProvider(LocalStrings provides strings, content = content)
}

object AppStrings {
    val current: Strings
        @Composable
        @ReadOnlyComposable
        get() = LocalStrings.current
}
