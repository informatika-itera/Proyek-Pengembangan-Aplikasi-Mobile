package id.pusakakata.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PusakaDeepBrown,
    onPrimary = Color.White,
    primaryContainer = PusakaBrown.copy(alpha = 0.15f),
    onPrimaryContainer = PusakaDeepBrown,
    secondary = PusakaRust,
    onSecondary = Color.White,
    secondaryContainer = PusakaRust.copy(alpha = 0.1f),
    onSecondaryContainer = PusakaDeepBrown,
    tertiary = PusakaPeach,
    onTertiary = Color.White,
    surface = SurfaceCard,
    onSurface = PusakaDeepBrown,
    background = PusakaCream,
    onBackground = PusakaDeepBrown,
    outline = PusakaBrown.copy(alpha = 0.5f),
    surfaceVariant = PusakaCream.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = PusakaPeach,
    onPrimary = PusakaDeepBrown,
    primaryContainer = PusakaDeepBrown,
    onPrimaryContainer = PusakaCream,
    secondary = PusakaRust,
    onSecondary = Color.White,
    tertiary = PusakaBrown,
    onTertiary = Color.White,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = PusakaCream,
    onSurface = PusakaCream,
    outline = PusakaBrown
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
