package com.kelazzz.app.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelazzz.app.presentation.screens.ai.AIScreen
import com.kelazzz.app.presentation.screens.home.HomeScreen
import com.kelazzz.app.presentation.screens.home.HomeViewModel
import com.kelazzz.app.presentation.screens.jadwal.JadwalScreen
import com.kelazzz.app.presentation.screens.kalender.KalenderScreen
import com.kelazzz.app.presentation.screens.presensi.PresensiScreen
import com.kelazzz.app.presentation.screens.profile.ProfileScreen
import com.kelazzz.app.presentation.screens.rekap.RekapScreen
import kelazzz.composeapp.generated.resources.Res
import kelazzz.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

// ==================== BOTTOM NAV ITEMS ====================

data class BottomNavItem(
    val route: Route,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Route.Home,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Route.DaftarPresensi,
        label = "Rekap",
        selectedIcon = Icons.Filled.Assessment,
        unselectedIcon = Icons.Outlined.Assessment
    ),
    BottomNavItem(
        route = Route.Presensi,
        label = "Presensi",
        selectedIcon = Icons.Filled.QrCodeScanner,
        unselectedIcon = Icons.Outlined.QrCodeScanner
    ),
    BottomNavItem(
        route = Route.AIAsisten,
        label = "AI",
        selectedIcon = Icons.Filled.Psychology,
        unselectedIcon = Icons.Outlined.Psychology
    ),
    BottomNavItem(
        route = Route.Kalender,
        label = "Jadwal",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )
)

// ==================== MAIN SCREEN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val innerNavController: NavHostController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val homeViewModel: HomeViewModel = koinViewModel()
    val userName by homeViewModel.userName.collectAsStateWithLifecycle()

    // Cek apakah sedang di Profile screen
    val isOnProfileScreen = currentDestination?.hasRoute(Route.Profile::class) == true

    // Determine current tab title
    val currentTitle = when {
        isOnProfileScreen -> "Profil"
        else -> bottomNavItems.find { item ->
            currentDestination?.hasRoute(item.route::class) == true
        }?.label ?: "KelazZz"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (isOnProfileScreen) {
                        // Tombol back saat di Profile screen
                        IconButton(onClick = { innerNavController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo KelazZz di samping judul (branding)
                        if (currentTitle == "Home" || isOnProfileScreen) {
                            Image(
                                painter = painterResource(Res.drawable.logo),
                                contentDescription = "Logo KelazZz",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                      
                    }
                },
                actions = {
                    // Ikon profil — navigasi ke Profile screen
                    if (!isOnProfileScreen) {
                        IconButton(
                            onClick = {
                                innerNavController.navigate(Route.Profile) {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Profil",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            // Sembunyikan bottom bar saat di Profile screen
            AnimatedVisibility(
                visible = !isOnProfileScreen,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hasRoute(item.route::class) == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    innerNavController.navigate(item.route) {
                                        // Pop up to start destination agar back stack bersih
                                        popUpTo(Route.Home) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = innerNavController,
            startDestination = Route.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Route.Home> {
                HomeScreen(viewModel = homeViewModel)
            }
            composable<Route.DaftarPresensi> {
                RekapScreen()
            }
            composable<Route.Presensi> {
                PresensiScreen()
            }
            composable<Route.AIAsisten> {
                AIScreen()
            }
            composable<Route.Kalender> {
                JadwalScreen()
            }
            composable<Route.Profile> {
                ProfileScreen(
                    onLogout = onLogout,
                    onBack = { innerNavController.popBackStack() }
                )
            }
        }
    }
}
