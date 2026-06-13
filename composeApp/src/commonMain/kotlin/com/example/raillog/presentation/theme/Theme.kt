package com.example.raillog.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary            = RailLogColors.PrimaryAction,
    onPrimary          = RailLogColors.White,
    primaryContainer   = RailLogColors.Brand100,
    onPrimaryContainer = RailLogColors.Brand700,

    secondary            = RailLogColors.Neutral700,
    onSecondary          = RailLogColors.White,
    secondaryContainer   = RailLogColors.Neutral100,
    onSecondaryContainer = RailLogColors.Neutral800,

    background    = RailLogColors.Background,
    onBackground  = RailLogColors.TextPrimary,
    surface       = RailLogColors.Surface,
    onSurface     = RailLogColors.TextPrimary,
    surfaceVariant    = RailLogColors.Neutral100,
    onSurfaceVariant  = RailLogColors.TextSecondary,

    outline      = RailLogColors.BorderDefault,
    outlineVariant = RailLogColors.BorderSubtle,

    error        = RailLogColors.Danger600,
    onError      = RailLogColors.White,
    errorContainer   = RailLogColors.Danger50,
    onErrorContainer = RailLogColors.Danger600,

    tertiary           = RailLogColors.Success600,
    onTertiary         = RailLogColors.White,
    tertiaryContainer  = RailLogColors.Success50,
    onTertiaryContainer = RailLogColors.Success600,

    scrim = Color(0x66000000)
)

val RailLogShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(6.dp),
    medium     = RoundedCornerShape(10.dp),
    large      = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

@Composable
fun RailLogTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = getTypography(),
        shapes      = RailLogShapes,
        content     = content
    )
}