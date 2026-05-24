package id.pusakakata

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import id.pusakakata.ui.navigation.AppNavHost
import id.pusakakata.ui.theme.PusakaKataTheme

@Composable
fun PusakaApp() {
    PusakaKataTheme {
        val navController = rememberNavController()
        AppNavHost(navController = navController)
    }
}
