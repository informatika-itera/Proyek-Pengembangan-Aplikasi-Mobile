package com.studyhub.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class NavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun StudyHubBottomBar(
    currentRoute: String?,
    onItemSelected: (Screen) -> Unit
) {
    val navItems = listOf(
        NavItem(Screen.Home, "Beranda", Icons.Default.Home, Icons.Outlined.Home),
        NavItem(Screen.Tasks, "Tugas", Icons.Default.Assignment, Icons.Outlined.Assignment),
        NavItem(Screen.Calendar, "Kalender", Icons.Default.CalendarMonth, Icons.Outlined.CalendarMonth),
        NavItem(Screen.Profile, "Profil", Icons.Default.Person, Icons.Outlined.Person)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.screen.route

            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item.screen) },
                icon = {
                    AnimatedContent(
                        targetState = selected,
                        transitionSpec = {
                            scaleIn(tween(150)) + fadeIn() togetherWith
                            scaleOut(tween(150)) + fadeOut()
                        },
                        label = "nav_icon_${item.label}"
                    ) { isSelected ->
                        Icon(
                            if (isSelected) item.selectedIcon
                            else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    }
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
