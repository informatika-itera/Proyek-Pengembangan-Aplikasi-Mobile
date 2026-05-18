package com.kosthub.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomItem(Routes.Home, "Home") { Icon(Icons.Filled.Home, contentDescription = null) },
        BottomItem(Routes.Favorites, "Favorites") { Icon(Icons.Filled.Favorite, contentDescription = null) },
        BottomItem(Routes.Contribute, "Contribute") { Icon(Icons.Filled.Settings, contentDescription = null) },
        BottomItem(Routes.Profile, "Profile") { Icon(Icons.Filled.Person, contentDescription = null) }
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = item.icon,
                label = { Text(text = item.label) }
            )
        }
    }
}
