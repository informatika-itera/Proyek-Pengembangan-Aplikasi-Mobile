package com.studymate.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.studymate.presentation.screens.calendar.CalendarScreen
import com.studymate.presentation.screens.home.HomeScreen
import com.studymate.presentation.screens.home.HomeViewModel
import com.studymate.presentation.screens.notes.NotesScreen
import com.studymate.presentation.screens.notes.NotesViewModel
import com.studymate.presentation.screens.profile.ProfileScreen
import com.studymate.presentation.screens.profile.ProfileViewModel
import com.studymate.presentation.screens.quiz.QuizScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Screen.NoteDetail.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple(Screen.Home, Icons.Default.Home, "Beranda"),
                        Triple(Screen.Notes, Icons.Default.EditNote, "Catatan"),
                        Triple(Screen.Quiz, Icons.Default.School, "Belajar"),
                        Triple(Screen.Calendar, Icons.Default.CalendarMonth, "Kalender"),
                        Triple(Screen.Profile, Icons.Default.Person, "Profil")
                    )
                    items.forEach { (screen, icon, label) ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = koinViewModel()
                HomeScreen(viewModel)
            }
            composable(Screen.Notes.route) {
                val viewModel: NotesViewModel = koinViewModel()
                NotesScreen(viewModel, onNavigateToDetail = {
                    navController.navigate(Screen.NoteDetail.createRoute(it))
                })
            }
            composable(Screen.Quiz.route) { QuizScreen() }
            composable(Screen.Calendar.route) { CalendarScreen() }
            composable(Screen.Profile.route) {
                val viewModel: ProfileViewModel = koinViewModel()
                ProfileScreen(viewModel)
            }
            composable(
                route = Screen.NoteDetail.route,
                arguments = listOf(navArgument(NavArgs.NOTE_ID) { type = NavType.LongType })
            ) {
                // Placeholder for Sprint 3
                Box(modifier = Modifier.padding(16.dp)) {
                    Text("Detail Catatan (Sprint 3)")
                }
            }
        }
    }
}
