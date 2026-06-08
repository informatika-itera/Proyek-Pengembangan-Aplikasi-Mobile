package id.my.sinanonym.mybawanggacha
 
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * Android MainActivity
 * 
 * Entry point untuk Android app.
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySystemBars(isSystemDarkTheme())
        setContent {
            App(onDarkThemeChange = ::applySystemBars)
        }
    }

    private fun applySystemBars(darkTheme: Boolean) {
        val background = if (darkTheme) DARK_BACKGROUND else LIGHT_BACKGROUND

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = background,
                darkScrim = background,
                detectDarkMode = { darkTheme }
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = background,
                darkScrim = background,
                detectDarkMode = { darkTheme }
            )
        )
    }

    private fun isSystemDarkTheme(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
    }

    private companion object {
        val LIGHT_BACKGROUND = 0xFFF2F7FD.toInt()
        val DARK_BACKGROUND = 0xFF0F0F12.toInt()
    }
}
 
