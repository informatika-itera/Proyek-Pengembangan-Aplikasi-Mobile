package com.studymate.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.studymate.presentation.screens.calendar.CalendarScreen
import com.studymate.presentation.screens.home.HomeScreen
import com.studymate.presentation.screens.home.HomeViewModel
import com.studymate.presentation.screens.notes.NoteDetailScreen
import com.studymate.presentation.screens.notes.NotesScreen
import com.studymate.presentation.screens.notes.NotesViewModel
import com.studymate.presentation.screens.profile.ProfileScreen
import com.studymate.presentation.screens.profile.ProfileViewModel
import com.studymate.presentation.screens.quiz.QuizScreen
import com.studymate.presentation.screens.quiz.QuizViewModel
import com.studymate.presentation.screens.quiz.SelectNoteForQuizScreen
import com.studymate.presentation.screens.splash.SplashScreen
import com.studymate.presentation.theme.PrimaryLight
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = (currentRoute != Screen.NoteDetail.route && 
                       currentRoute != Screen.Splash.route && 
                       currentRoute != Screen.SelectNoteForQuiz.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(72.dp)
                    ) {
                        val items = listOf(
                            Triple(Screen.Home, Icons.Default.Home, "Home"),
                            Triple(Screen.Notes, Icons.Default.EditNote, "Notes"),
                            Triple(Screen.Quiz, Icons.Default.School, "Quiz"),
                            Triple(Screen.Calendar, Icons.Default.CalendarMonth, "Planner"),
                            Triple(Screen.Profile, Icons.Default.Person, "Profile")
                        )
                        items.forEach { (screen, icon, label) ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.Home.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(24.dp),
                                        tint = if (isSelected) PrimaryLight else Color.Gray
                                    )
                                },
                                label = {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = PrimaryLight.copy(alpha = 0.12f),
                                    selectedIconColor = PrimaryLight,
                                    selectedTextColor = PrimaryLight,
                                    unselectedIconColor = Color.Gray,
                                    unselectedTextColor = Color.Gray
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { _ ->
        Box(modifier = Modifier.padding(bottom = 0.dp)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier.padding(bottom = if (showBottomBar) 88.dp else 0.dp)
            ) {
                composable(Screen.Splash.route) {
                    SplashScreen(
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.Home.route) {
                    val viewModel: HomeViewModel = koinViewModel()
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToNoteDetail = { noteId ->
                            navController.navigate(Screen.NoteDetail.createRoute(noteId))
                        },
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(Screen.Notes.route) {
                    val viewModel: NotesViewModel = koinViewModel()
                    NotesScreen(viewModel, onNavigateToDetail = {
                        navController.navigate(Screen.NoteDetail.createRoute(it))
                    })
                }
                composable(Screen.Quiz.route) { 
                    val viewModel: QuizViewModel = koinViewModel()
                    QuizScreen(
                        viewModel = viewModel,
                        onNavigateToSelectNote = { navController.navigate(Screen.SelectNoteForQuiz.route) }
                    ) 
                }
                composable(Screen.SelectNoteForQuiz.route) {
                    val viewModel: QuizViewModel = koinViewModel()
                    SelectNoteForQuizScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNoteSelected = { note ->
                            viewModel.startQuiz(note)
                            navController.popBackStack()
                        }
                    )
                }
                composable(Screen.Calendar.route) { CalendarScreen() }
                composable(Screen.Profile.route) {
                    val viewModel: ProfileViewModel = koinViewModel()
                    ProfileScreen(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle
                    )
                }
                composable(
                    route = Screen.NoteDetail.route,
                    arguments = listOf(navArgument(NavArgs.NOTE_ID) { type = NavType.LongType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getLong(NavArgs.NOTE_ID) ?: -1L
                    val viewModel: NotesViewModel = koinViewModel()
                    NoteDetailScreen(
                        noteId = noteId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
