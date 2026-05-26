package com.example.fitgen.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        label          = "Home",
        selectedIcon   = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    WORKOUT(
        label          = "Latihan",
        selectedIcon   = Icons.Filled.FitnessCenter,
        unselectedIcon = Icons.Outlined.FitnessCenter
    ),
    NUTRITION(
        label          = "Nutrisi",
        selectedIcon   = Icons.Filled.RestaurantMenu,
        unselectedIcon = Icons.Outlined.RestaurantMenu
    )
}

@Composable
fun BottomNavBar(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    NavigationBar {
        BottomNavTab.entries.forEach { tab ->
            val selected = tab == currentTab
            NavigationBarItem(
                selected = selected,
                onClick  = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector        = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) }
            )
        }
    }
}