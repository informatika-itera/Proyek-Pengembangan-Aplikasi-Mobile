package com.studyhub.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun StudyHubBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        val items = listOf(
            Triple(Screen.Home, Icons.Outlined.Home, Icons.Filled.Home) to "Home",
            Triple(Screen.Tasks, Icons.Outlined.Assignment, Icons.Filled.Assignment) to "Tasks",
            Triple(Screen.Calendar, Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth) to "Kalender",
            Triple(Screen.Profile, Icons.Outlined.Person, Icons.Filled.Person) to "Profil"
        )
        items.forEach { (triple, label) ->
            val (screen, outlineIcon, filledIcon) = triple
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Home.route)
                    }
                },
                icon = {
                    Icon(if (selected) filledIcon else outlineIcon, label)
                },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
