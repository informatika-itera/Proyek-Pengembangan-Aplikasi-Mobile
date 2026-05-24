package com.example.rosea.presentation.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rosea.presentation.navigation.AppNavHost
import com.example.rosea.presentation.navigation.Routes

// 👇 Kita ubah namanya menjadi BottomNavItem agar tidak bentrok dengan sistem Android
data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // 👇 Daftar menu menggunakan nama yang baru
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, Routes.HOME),
        BottomNavItem("AI Advisor", Icons.Default.AutoAwesome, Routes.AI_ASSISTANT),
        BottomNavItem("Keranjang", Icons.Default.ShoppingBag, Routes.CART),
        BottomNavItem("Profil", Icons.Default.Person, Routes.PROFILE)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Routes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding) // 👈 Modifier agar tidak tenggelam
        )
    }
}