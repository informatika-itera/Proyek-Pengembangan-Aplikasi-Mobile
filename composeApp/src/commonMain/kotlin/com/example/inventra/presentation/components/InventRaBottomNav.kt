package com.example.inventra.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventra.presentation.navigation.Routes

@Composable
fun InventRaBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Menu Dashboard / Home
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) },
            selected = currentRoute == Routes.Dashboard.route,
            onClick = { onNavigate(Routes.Dashboard.route) },
            colors = bottomNavColors()
        )

        // Menu Items / Catalog
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory2, contentDescription = "Items") },
            label = { Text("Items", fontSize = 10.sp) },
            selected = currentRoute == Routes.Catalog.route,
            onClick = { onNavigate(Routes.Catalog.route) },
            colors = bottomNavColors()
        )

        // Menu Ask AI
        NavigationBarItem(
            icon = { Icon(Icons.Default.SmartToy, contentDescription = "Ask AI") },
            label = { Text("Ask AI", fontSize = 10.sp) },
            selected = currentRoute == Routes.AskAI.route,
            onClick = { onNavigate(Routes.AskAI.route) },
            colors = bottomNavColors()
        )

        // Menu History
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("History", fontSize = 10.sp) },
            selected = currentRoute == Routes.History.route,
            onClick = { onNavigate(Routes.History.route) },
            colors = bottomNavColors()
        )

        // Menu Profile
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 10.sp) },
            selected = currentRoute == Routes.Profile.route,
            onClick = { onNavigate(Routes.Profile.route) },
            colors = bottomNavColors()
        )
    }
}

// Fungsi untuk menyeragamkan warna Bottom Nav agar sesuai tema (termasuk Dark Mode)
@Composable
private fun bottomNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
    selectedTextColor = MaterialTheme.colorScheme.secondary,
    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)