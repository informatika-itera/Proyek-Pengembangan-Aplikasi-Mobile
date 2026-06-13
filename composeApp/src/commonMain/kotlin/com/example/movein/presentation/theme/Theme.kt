package com.example.movein.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MoveInColors(
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    val surfacePrimary: Color,
    val surfaceSecondary: Color,
    val surfaceTertiary: Color,
    val cardPrimary: Color,
    val cardSecondary: Color,
    val borderSubtle: Color,
    val primary: Color,
    val primarySoft: Color,
    val secondary: Color,
    val accentPink: Color,
    val accentBlue: Color,
    val accentGreen: Color,
    val warningYellow: Color,
    val errorRed: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val navBackground: Color,
    val navActive: Color
)

val LocalMoveInColors = staticCompositionLocalOf {
    MoveInColors(
        backgroundPrimary = Color.Unspecified,
        backgroundSecondary = Color.Unspecified,
        surfacePrimary = Color.Unspecified,
        surfaceSecondary = Color.Unspecified,
        surfaceTertiary = Color.Unspecified,
        cardPrimary = Color.Unspecified,
        cardSecondary = Color.Unspecified,
        borderSubtle = Color.Unspecified,
        primary = Color.Unspecified,
        primarySoft = Color.Unspecified,
        secondary = Color.Unspecified,
        accentPink = Color.Unspecified,
        accentBlue = Color.Unspecified,
        accentGreen = Color.Unspecified,
        warningYellow = Color.Unspecified,
        errorRed = Color.Unspecified,
        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        textMuted = Color.Unspecified,
        navBackground = Color.Unspecified,
        navActive = Color.Unspecified
    )
}

private val DarkMoveInColors = MoveInColors(
    backgroundPrimary = DarkBackgroundPrimary,
    backgroundSecondary = DarkBackgroundSecondary,
    surfacePrimary = DarkSurfacePrimary,
    surfaceSecondary = DarkSurfaceSecondary,
    surfaceTertiary = DarkSurfaceTertiary,
    cardPrimary = DarkCardPrimary,
    cardSecondary = DarkCardSecondary,
    borderSubtle = DarkBorderSubtle,
    primary = DarkPrimary,
    primarySoft = DarkPrimarySoft,
    secondary = DarkSecondary,
    accentPink = DarkAccentPink,
    accentBlue = DarkAccentBlue,
    accentGreen = DarkAccentGreen,
    warningYellow = DarkWarningYellow,
    errorRed = DarkErrorRed,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    navBackground = DarkNavBackground,
    navActive = DarkNavActive
)

private val LightMoveInColors = MoveInColors(
    backgroundPrimary = LightBackgroundPrimary,
    backgroundSecondary = LightBackgroundSecondary,
    surfacePrimary = LightSurfacePrimary,
    surfaceSecondary = LightSurfaceSecondary,
    surfaceTertiary = LightSurfaceTertiary,
    cardPrimary = LightCardPrimary,
    cardSecondary = LightCardSecondary,
    borderSubtle = LightBorderSubtle,
    primary = LightPrimary,
    primarySoft = LightPrimarySoft,
    secondary = LightSecondary,
    accentPink = LightAccentPink,
    accentBlue = LightAccentBlue,
    accentGreen = LightAccentGreen,
    warningYellow = LightWarningYellow,
    errorRed = LightErrorRed,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,
    navBackground = LightNavBackground,
    navActive = LightNavActive
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkTextPrimary,
    primaryContainer = DarkPrimarySoft,
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkTextPrimary,
    background = DarkBackgroundPrimary,
    onBackground = DarkTextPrimary,
    surface = DarkSurfacePrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceSecondary,
    onSurfaceVariant = DarkTextSecondary,
    error = DarkErrorRed,
    outline = DarkBorderSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightSurfacePrimary,
    primaryContainer = LightPrimarySoft,
    onPrimaryContainer = LightPrimary,
    secondary = LightSecondary,
    onSecondary = LightSurfacePrimary,
    background = LightBackgroundPrimary,
    onBackground = LightTextPrimary,
    surface = LightSurfacePrimary,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceSecondary,
    onSurfaceVariant = LightTextSecondary,
    error = LightErrorRed,
    outline = LightBorderSubtle
)

@Composable
fun NoteAITheme(
    darkTheme: Boolean = true, // Default Dark Mode as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val moveInColors = if (darkTheme) DarkMoveInColors else LightMoveInColors

    CompositionLocalProvider(
        LocalMoveInColors provides moveInColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MoveInTypography,
            shapes = MoveInShapes,
            content = content
        )
    }
}

object MoveInTheme {
    val colors: MoveInColors
        @Composable
        get() = LocalMoveInColors.current

    val typography: Typography
        @Composable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        get() = MaterialTheme.shapes
}
