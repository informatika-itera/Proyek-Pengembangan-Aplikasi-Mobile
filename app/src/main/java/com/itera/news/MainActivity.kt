package com.itera.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.itera.news.presentation.navigation.NavGraph
import com.itera.news.presentation.navigation.Screen
import com.itera.news.ui.theme.MyApplicationTheme
import com.itera.news.ui.theme.neumorphicShadow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val showBottomBar = currentDestination?.route in listOf(
                    Screen.Home.route,
                    Screen.Bookmark.route,
                    Screen.About.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(
                                currentDestination = currentDestination,
                                onNavigate = { screen ->
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentDestination: androidx.navigation.NavDestination?,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        Triple(Screen.Home, "Home", Icons.Default.Home),
        Triple(Screen.Bookmark, "Simpan", Icons.Default.Star),
        Triple(Screen.About, "Profil", Icons.Default.Person)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .neumorphicShadow(offset = 4.dp, blur = 10.dp, cornerRadius = 24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (screen, label, icon) ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(screen) }
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}