package com.example.gamenews.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.gamenews.presentation.screens.home.HomeScreen
import com.example.gamenews.presentation.screens.wishlist.WishlistScreen

@Composable
fun MainScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit
) {
    var currentTab by remember { mutableStateOf(0) }
    val primaryColor = Color(0xFF6750A4)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                backgroundColor = primaryColor,
                contentColor = Color.White
            ) {
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Wishlist") },
                    label = { Text("Wishlist") },
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                0 -> HomeScreen(onNavigateToDetail = onNavigateToDetail, onNavigateToAI = onNavigateToAI)
                1 -> WishlistScreen(onNavigateToDetail = onNavigateToDetail)
                2 -> SettingPlaceholderScreen(primaryColor = primaryColor)
            }
        }
    }
}

@Composable
fun SettingPlaceholderScreen(primaryColor: Color) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                backgroundColor = primaryColor,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
        }
    }
}