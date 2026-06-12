package com.example.rewind.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.rewind.presentation.screens.addmovie.AddMovieScreen
import com.example.rewind.presentation.screens.ai.AIAssistantScreen
import com.example.rewind.presentation.screens.detail.DetailScreen
import com.example.rewind.presentation.screens.home.HomeScreen
import com.example.rewind.presentation.screens.profile.ProfileScreen
import com.example.rewind.presentation.screens.settings.SettingsScreen
import com.example.rewind.presentation.screens.splash.SplashScreen
import com.example.rewind.presentation.theme.BorderGold
import com.example.rewind.presentation.theme.GoldAmber

private val bottomNavRoutes: List<Route> = listOf(
    Route.Home,
    Route.AIAssistant,
    Route.Settings,
    Route.Profile
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavRoutes.any { route ->
        currentDestination?.hasRoute(route::class) == true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                RewindBottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Splash,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.Splash> {
                SplashScreen(
                    onFinished = {
                        navController.navigate(Route.Home) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    }
                )
            }

            composable<Route.Home> {
                HomeScreen(
                    onAddClick = { navController.navigate(Route.AddMovie()) },
                    onMovieClick = { id -> navController.navigate(Route.MovieDetail(movieId = id)) }
                )
            }

            composable<Route.AddMovie> { backStackEntry ->
                val route = backStackEntry.toRoute<Route.AddMovie>()
                AddMovieScreen(
                    movieId = route.movieId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<Route.MovieDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<Route.MovieDetail>()
                DetailScreen(
                    movieId = route.movieId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id -> navController.navigate(Route.AddMovie(movieId = id)) }
                )
            }

            composable<Route.AIAssistant> {
                AIAssistantScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<Route.Profile> {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<Route.Settings> {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private data class BottomNavItem(
    val route: Route,
    val icon: ImageVector,
    val label: String
)

@Composable
private fun RewindBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    val items = listOf(
        BottomNavItem(Route.Home, Icons.Rounded.Home, "Home"),
        BottomNavItem(Route.AIAssistant, Icons.Rounded.Psychology, "AI"),
        BottomNavItem(Route.Settings, Icons.Rounded.Settings, "Settings"),
        BottomNavItem(Route.Profile, Icons.Rounded.Person, "Profile")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            BorderGold.copy(alpha = 0.3f),
                            GoldAmber.copy(alpha = 0.2f),
                            BorderGold.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentDestination?.hasRoute(item.route::class) == true
                BottomNavItemView(
                    item = item,
                    isSelected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo<Route.Home> { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .blur(12.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
            }
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = item.label,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            letterSpacing = 0.3.sp
        )
    }
}