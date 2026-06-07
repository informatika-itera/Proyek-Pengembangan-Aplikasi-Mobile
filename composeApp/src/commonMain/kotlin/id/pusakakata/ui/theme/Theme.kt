package id.pusakakata.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryFixed,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryFixed,
    secondary = SecondaryGold,
    onSecondary = Color.White,
    secondaryContainer = SecondaryGold.copy(alpha = 0.1f),
    onSecondaryContainer = PrimaryFixed,
    tertiary = AccentTeal,
    onTertiary = Color.White,
    surface = SurfaceCard,
    onSurface = PrimaryFixed,
    background = WarmBackground,
    onBackground = PrimaryFixed,
    outline = PrimaryLight.copy(alpha = 0.5f),
    surfaceVariant = Color(0xFFF5F0E6)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD7CCC8),
    onPrimary = PrimaryFixed,
    secondary = SecondaryGold,
    background = Color(0xFF212121),
    surface = Color(0xFF2D2D21),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun PusakaKataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
