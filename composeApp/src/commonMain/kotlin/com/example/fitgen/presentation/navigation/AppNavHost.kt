package com.example.fitgen.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fitgen.presentation.screens.addnote.AddNoteScreen
import com.example.fitgen.presentation.screens.ai.AIAssistantScreen
import com.example.fitgen.presentation.screens.detail.NoteDetailScreen
import com.example.fitgen.presentation.screens.home.HomeScreen
import com.example.fitgen.presentation.screens.profile.ProfileScreen
import com.example.fitgen.presentation.screens.workout.AddWorkoutScreen
import com.example.fitgen.presentation.screens.workout.WorkoutListScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // BottomNav hanya tampil di top-level screens
    val showBottomBar = currentDestination?.let {
        it.hasRoute(Route.Home::class) || 
        it.hasRoute(Route.WorkoutList::class) ||
        it.hasRoute(Route.Profile::class)
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Route.Home::class) == true,
                        onClick = { navigationActions.navigateToHome() },
                        icon = {
                            Icon(
                                if (currentDestination?.hasRoute(Route.Home::class) == true)
                                    Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Catatan"
                            )
                        },
                        label = { Text("Catatan") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Route.WorkoutList::class) == true,
                        onClick = { navigationActions.navigateToWorkoutList() },
                        icon = {
                            Icon(
                                if (currentDestination?.hasRoute(Route.WorkoutList::class) == true)
                                    Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter,
                                contentDescription = "Latihan"
                            )
                        },
                        label = { Text("Latihan") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Route.Profile::class) == true,
                        onClick = { navigationActions.navigateToProfile() },
                        icon = {
                            Icon(
                                if (currentDestination?.hasRoute(Route.Profile::class) == true)
                                    Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profil"
                            )
                        },
                        label = { Text("Profil") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home,
            modifier = modifier
        ) {
            composable<Route.Home> {
                HomeScreen(
                    onNavigateToAddNote = { navigationActions.navigateToAddNote() },
                    onNavigateToDetail = { noteId -> navigationActions.navigateToNoteDetail(noteId) },
                    onNavigateToAI = { navigationActions.navigateToAIAssistant() }
                )
            }

            composable<Route.AddNote> { backStackEntry ->
                val route: Route.AddNote = backStackEntry.toRoute()
                AddNoteScreen(
                    noteId = route.noteId,
                    onNavigateBack = { navigationActions.navigateBack() },
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
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToEdit = { navigationActions.navigateToAddNote(route.noteId) },
                    onShare = { _ -> }
                )
            }

            composable<Route.AIAssistant> { backStackEntry ->
                val route: Route.AIAssistant = backStackEntry.toRoute()
                AIAssistantScreen(
                    noteId = route.noteId,
                    initialText = route.initialText,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onApplyResult = null
                )
            }

            // ── Sprint 2: Workout screens ──
            composable<Route.WorkoutList> {
                WorkoutListScreen(
                    onNavigateToAddWorkout = { navigationActions.navigateToAddWorkout() },
                    onNavigateToDetail = { /* TODO: WorkoutDetailScreen */ }
                )
            }

            composable<Route.AddWorkout> {
                AddWorkoutScreen(
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) { inclusive = true }
            }
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

        override fun navigateToWorkoutList() {
            navController.navigate(Route.WorkoutList)
        }

        override fun navigateToAddWorkout() {
            navController.navigate(Route.AddWorkout)
        }

        override fun navigateToProfile() {
            navController.navigate(Route.Profile)
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}
