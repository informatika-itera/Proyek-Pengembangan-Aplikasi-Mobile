package com.example.fitkos.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fitkos.data.local.datastore.UserPreferences
import com.example.fitkos.presentation.screens.addnote.AddNoteScreen
import com.example.fitkos.presentation.screens.ai.AIAssistantScreen
import com.example.fitkos.presentation.screens.dashboard.DashboardScreen
import com.example.fitkos.presentation.screens.detail.NoteDetailScreen
import com.example.fitkos.presentation.screens.exercise.ExerciseScreen
import com.example.fitkos.presentation.screens.home.HomeScreen
import com.example.fitkos.presentation.screens.settings.SettingsScreen
import com.example.fitkos.presentation.screens.splash.SplashScreen
import com.example.fitkos.presentation.screens.watertracker.WaterTrackerScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Jangan pakai substringAfterLast(".") karena route typed-navigation bisa punya argumen.
    // Pakai raw route lalu cek dengan contains().
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userPreferences: UserPreferences = koinInject()
    val userName by userPreferences.userName.collectAsStateWithLifecycle(initialValue = "Sobat Kos")

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRoute.shouldEnableDrawerGesture(),
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = userName.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Jaga sehat, produktif!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Beranda") },
                    selected = currentRoute.isRoute("Dashboard"),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigationActions.navigateToDashboard()
                    },
                    icon = { Icon(Icons.Default.Home, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Catatan Makan") },
                    selected = currentRoute.isRoute("Home"),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigationActions.navigateToHome()
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Tracker Air") },
                    selected = currentRoute.isRoute("WaterTracker"),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigationActions.navigateToWaterTracker()
                    },
                    icon = { Icon(Icons.Default.WaterDrop, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Olahraga") },
                    selected = currentRoute.isRoute("Exercise"),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigationActions.navigateToExercise()
                    },
                    icon = { Icon(Icons.Default.Timer, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Asisten AI") },
                    selected = currentRoute.isRoute("AIAssistant"),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigationActions.navigateToAIAssistant()
                    },
                    icon = { Icon(Icons.Outlined.AutoAwesome, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = currentRoute.isRoute("Settings"),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigationActions.navigateToSettings()
                    },
                    icon = { Icon(Icons.Default.Settings, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (currentRoute.shouldShowAppTopBar()) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "FitKos",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        },
                        actions = {
                            // Kosongkan bagian actions untuk menghapus ikon lonceng
                        }
                    )
                }
            },
            bottomBar = {
                if (currentRoute.shouldShowBottomBar()) {
                    FitKosBottomBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigateTopLevel(route)
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Route.Splash,
                modifier = modifier.padding(paddingValues)
            ) {
                composable<Route.Splash> {
                    SplashScreen(
                        onNavigateToDashboard = {
                            navController.navigate(Route.Dashboard) {
                                popUpTo(Route.Splash) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable<Route.Dashboard> {
                    DashboardScreen(
                        onNavigateToMealLog = {
                            navigationActions.navigateToHome()
                        },
                        onNavigateToAddMeal = {
                            navigationActions.navigateToAddNote()
                        },
                        onNavigateToWaterTracker = {
                            navigationActions.navigateToWaterTracker()
                        },
                        onNavigateToExercise = {
                            navigationActions.navigateToExercise()
                        },
                        onNavigateToAI = {
                            navigationActions.navigateToAIAssistant(
                                initialText = """
                                    Saya penghuni kos dan ingin menjaga hidup sehat dengan budget terbatas.
                                    Tolong beri saran makanan sehat hemat, kebiasaan minum air, dan olahraga ringan yang realistis untuk hari ini.
                                """.trimIndent()
                            )
                        }
                    )
                }

                composable<Route.Home> {
                    HomeScreen(
                        onNavigateToAddNote = {
                            navigationActions.navigateToAddNote()
                        },
                        onNavigateToDetail = { noteId ->
                            navigationActions.navigateToNoteDetail(noteId)
                        },
                        onNavigateToAI = {
                            navigationActions.navigateToAIAssistant()
                        }
                    )
                }

                composable<Route.WaterTracker> {
                    WaterTrackerScreen(
                        onNavigateBack = {
                            navigationActions.navigateBack()
                        }
                    )
                }

                composable<Route.Exercise> {
                    ExerciseScreen(
                        onNavigateBack = {
                            navigationActions.navigateBack()
                        }
                    )
                }

                composable<Route.Settings> {
                    SettingsScreen(
                        onNavigateBack = {
                            navigationActions.navigateBack()
                        },
                        onLogout = {
                            navController.navigate(Route.Splash) {
                                popUpTo(Route.Dashboard) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable<Route.AddNote> { backStackEntry ->
                    val route: Route.AddNote = backStackEntry.toRoute()

                    AddNoteScreen(
                        noteId = route.noteId,
                        onNavigateBack = {
                            navigationActions.navigateBack()
                        },
                        onNavigateToAI = { text ->
                            navigationActions.navigateToAIAssistant(
                                noteId = route.noteId,
                                initialText = text
                            )
                        }
                    )
                }

                composable<Route.NoteDetail> { backStackEntry ->
                    val route: Route.NoteDetail = backStackEntry.toRoute()

                    NoteDetailScreen(
                        noteId = route.noteId,
                        onNavigateBack = {
                            navigationActions.navigateBack()
                        },
                        onNavigateToEdit = {
                            navigationActions.navigateToAddNote(route.noteId)
                        },
                        onShare = { _ -> }
                    )
                }

                composable<Route.AIAssistant> { backStackEntry ->
                    val route: Route.AIAssistant = backStackEntry.toRoute()

                    AIAssistantScreen(
                        noteId = route.noteId,
                        initialText = route.initialText,
                        onNavigateBack = {
                            navigationActions.navigateBack()
                        },
                        onApplyResult = null
                    )
                }
            }
        }
    }
}

@Composable
private fun FitKosBottomBar(
    currentRoute: String?,
    onNavigate: (Route) -> Unit
) {
    val items = listOf(
        BottomBarItem(
            label = "Home",
            icon = Icons.Default.Home,
            route = Route.Dashboard,
            routeKey = "Dashboard"
        ),
        BottomBarItem(
            label = "Catatan",
            icon = Icons.AutoMirrored.Filled.List,
            route = Route.Home,
            routeKey = "Home"
        ),
        BottomBarItem(
            label = "Air",
            icon = Icons.Default.WaterDrop,
            route = Route.WaterTracker,
            routeKey = "WaterTracker"
        ),
        BottomBarItem(
            label = "AI",
            icon = Icons.Outlined.AutoAwesome,
            route = Route.AIAssistant(),
            routeKey = "AIAssistant"
        ),
        BottomBarItem(
            label = "Olahraga",
            icon = Icons.Default.Timer,
            route = Route.Exercise,
            routeKey = "Exercise"
        )
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute.isRoute(item.routeKey),
                onClick = {
                    item.route?.let { route ->
                        onNavigate(route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(item.label)
                }
            )
        }
    }
}

private data class BottomBarItem(
    val label: String,
    val icon: ImageVector,
    val route: Route?,
    val routeKey: String
)

private fun String?.isRoute(routeKey: String): Boolean {
    return this?.contains(routeKey) == true
}

private fun String?.shouldShowBottomBar(): Boolean {
    return this.isRoute("Dashboard") ||
            this.isRoute("Home") ||
            this.isRoute("WaterTracker") ||
            this.isRoute("AIAssistant") ||
            this.isRoute("Exercise") ||
            this.isRoute("Settings")
}

private fun String?.shouldEnableDrawerGesture(): Boolean {
    return this.isRoute("Dashboard") ||
            this.isRoute("Home") ||
            this.isRoute("WaterTracker") ||
            this.isRoute("AIAssistant") ||
            this.isRoute("Exercise") ||
            this.isRoute("Settings")
}

private fun String?.shouldShowAppTopBar(): Boolean {
    return this.isRoute("Dashboard")
}

private fun NavHostController.navigateTopLevel(route: Route) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true

        popUpTo(Route.Dashboard) {
            saveState = true
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToDashboard() {
            navController.navigateTopLevel(Route.Dashboard)
        }

        override fun navigateToHome() {
            navController.navigateTopLevel(Route.Home)
        }

        override fun navigateToAddNote(noteId: Long?) {
            navController.navigate(Route.AddNote(noteId))
        }

        override fun navigateToNoteDetail(noteId: Long) {
            navController.navigate(Route.NoteDetail(noteId))
        }

        override fun navigateToAIAssistant(noteId: Long?, initialText: String?) {
            navController.navigate(Route.AIAssistant(noteId, initialText))
        }

        override fun navigateToWaterTracker() {
            navController.navigateTopLevel(Route.WaterTracker)
        }

        override fun navigateToExercise() {
            navController.navigateTopLevel(Route.Exercise)
        }

        override fun navigateToSettings() {
            navController.navigateTopLevel(Route.Settings)
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}