package com.example.fitgen.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fitgen.presentation.screens.ai.AIAssistantScreen
import com.example.fitgen.presentation.screens.ai.DynamicWorkoutScreen
import com.example.fitgen.presentation.screens.home.HomeDashboardScreen
import com.example.fitgen.presentation.screens.home.HomeScreen
import com.example.fitgen.presentation.screens.nutrition.AddMealScreen
import com.example.fitgen.presentation.screens.nutrition.NutritionScreen
import com.example.fitgen.presentation.screens.profile.ProfileScreen
import com.example.fitgen.presentation.screens.profile.EditProfileScreen
import com.example.fitgen.presentation.screens.workout.AddWorkoutScreen
import com.example.fitgen.presentation.screens.workout.WorkoutListScreen
import com.example.fitgen.presentation.screens.workout.ActiveSessionScreen
import com.example.fitgen.presentation.screens.workout.ActiveSessionViewModel
import com.example.fitgen.presentation.screens.workout.ExerciseDetailScreen
import org.koin.compose.viewmodel.koinViewModel

private val topLevelRoutes = listOf(
    Route.Home::class,
    Route.Dashboard::class,
    Route.WorkoutList::class,
    Route.Nutrition::class,
    Route.Profile::class
)

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = topLevelRoutes.any { routeClass ->
        currentDestination?.hasRoute(routeClass) == true
    }

    val maroon      = Color(0xFF800000)
    val maroonDark  = Color(0xFF5A0000)
    val surfaceColor = MaterialTheme.colorScheme.surface

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .drawBehind {
                            val path = Path().apply {
                                val width = size.width
                                val height = size.height
                                val center = width / 2f
                                val bumpRadius = 36.dp.toPx() // Cutout radius (slightly larger than FAB)
                                val curveOffset = 24.dp.toPx() 
                                
                                moveTo(0f, 0f)
                                lineTo(center - bumpRadius - curveOffset, 0f)
                                
                                // Dip DOWN into the bar to create a cutout hole
                                cubicTo(
                                    center - bumpRadius, 0f, 
                                    center - bumpRadius, bumpRadius, // Control point moves DOWN
                                    center, bumpRadius // End point is at the bottom of the cutout
                                )
                                cubicTo(
                                    center + bumpRadius, bumpRadius, // Starts from bottom
                                    center + bumpRadius, 0f, // Control point moves UP
                                    center + bumpRadius + curveOffset, 0f 
                                )
                                
                                lineTo(width, 0f)
                                lineTo(width, height)
                                lineTo(0f, height)
                                close()
                            }
                            
                            // Light top shadow/border
                            drawPath(
                                path = path,
                                color = Color.Black.copy(alpha = 0.05f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                            )
                            // Background
                            drawPath(
                                path = path,
                                color = surfaceColor
                            )
                        }
                ) {
                    // Navigation Items Row
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavItem(
                            icon = if (currentDestination?.hasRoute(Route.Home::class) == true) Icons.Filled.Home else Icons.Outlined.Home,
                            label = "Beranda",
                            selected = currentDestination?.hasRoute(Route.Home::class) == true,
                            onClick = { navigationActions.navigateToHome() },
                            modifier = Modifier.weight(1f)
                        )
                        NavItem(
                            icon = if (currentDestination?.hasRoute(Route.WorkoutList::class) == true) Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter,
                            label = "Latihan",
                            selected = currentDestination?.hasRoute(Route.WorkoutList::class) == true,
                            onClick = { navigationActions.navigateToWorkoutList() },
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Spacer for Center FAB Cutout
                        Spacer(modifier = Modifier.weight(1f))
                        
                        NavItem(
                            icon = if (currentDestination?.hasRoute(Route.Nutrition::class) == true) Icons.Filled.RestaurantMenu else Icons.Outlined.RestaurantMenu,
                            label = "Nutrisi",
                            selected = currentDestination?.hasRoute(Route.Nutrition::class) == true,
                            onClick = { navigationActions.navigateToNutrition() },
                            modifier = Modifier.weight(1f)
                        )
                        NavItem(
                            icon = if (currentDestination?.hasRoute(Route.Profile::class) == true) Icons.Filled.Person else Icons.Outlined.Person,
                            label = "Profil",
                            selected = currentDestination?.hasRoute(Route.Profile::class) == true,
                            onClick = { navigationActions.navigateToProfile() },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Floating Camera Button (Ditempatkan di tengah lekukan)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-28).dp) // Offset by half its size so its center is exactly on the top edge
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF800000))
                            .clickable { navigationActions.navigateToAddMeal() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Tambah Makanan via Kamera",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Route.Splash,
            modifier         = modifier.padding(innerPadding)
        ) {
            composable<Route.Splash> {
                com.example.fitgen.presentation.screens.splash.SplashScreen(
                    onNavigateToHome = {
                        navigationActions.navigateToHome()
                    },
                    onNavigateToOnboarding = {
                        navigationActions.navigateToOnboarding()
                    }
                )
            }
            composable<Route.Onboarding> {
                com.example.fitgen.presentation.screens.onboarding.OnboardingScreen(
                    onOnboardingComplete = {
                        navigationActions.navigateToHome()
                    }
                )
            }
            composable<Route.Home> {
                HomeDashboardScreen(
                    onNavigateToWorkout        = { navigationActions.navigateToWorkoutList() },
                    onNavigateToNutrition      = { navigationActions.navigateToNutrition() },
                    onNavigateToDynamicWorkout = { navigationActions.navigateToDynamicWorkout() },
                    onNavigateToExerciseDetail = { name, bodyPart, gifUrl, instructions ->
                        navigationActions.navigateToExerciseDetail(name, bodyPart, gifUrl, instructions)
                    },
                    onNavigateToChallengeDetail = { id -> navigationActions.navigateToChallengeDetail(id) }
                )
            }
            composable<Route.AIAssistant> { backStackEntry ->
                val route: Route.AIAssistant = backStackEntry.toRoute()
                AIAssistantScreen(
                    noteId         = null,
                    initialText    = route.initialText,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onApplyResult  = null
                )
            }
            composable<Route.Dashboard> {
                HomeDashboardScreen(
                    onNavigateToWorkout        = { navigationActions.navigateToWorkoutList() },
                    onNavigateToNutrition      = { navigationActions.navigateToNutrition() },
                    onNavigateToDynamicWorkout = { navigationActions.navigateToDynamicWorkout() },
                    onNavigateToExerciseDetail = { name, bodyPart, gifUrl, instructions ->
                        navigationActions.navigateToExerciseDetail(name, bodyPart, gifUrl, instructions)
                    },
                    onNavigateToChallengeDetail = { id -> navigationActions.navigateToChallengeDetail(id) }
                )
            }
            composable<Route.WorkoutList> {
                WorkoutListScreen(
                    onNavigateToAddWorkout = { navigationActions.navigateToDynamicWorkout() },
                    onNavigateToDetail     = { },
                    onNavigateToApiExerciseDetail = { name, bodyPart, gifUrl, instructions ->
                        navigationActions.navigateToExerciseDetail(name, bodyPart, gifUrl, instructions)
                    }
                )
            }
            composable<Route.AddWorkout> {
                AddWorkoutScreen(onNavigateBack = { navigationActions.navigateBack() })
            }
            composable<Route.Nutrition> {
                NutritionScreen(onNavigateToAddMeal = { navigationActions.navigateToAddMeal() })
            }
            composable<Route.AddMeal> {
                AddMealScreen(onNavigateBack = { navigationActions.navigateBack() })
            }
            composable<Route.Profile> {
                ProfileScreen(
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToEditProfile = { navigationActions.navigateToEditProfile() },
                    onLogoutSuccess = { navigationActions.navigateToOnboarding() }
                )
            }
            composable<Route.EditProfile> {
                EditProfileScreen(
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }
            composable<Route.DynamicWorkout> {
                DynamicWorkoutScreen(
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }
            composable<Route.ExerciseDetail> { backStackEntry ->
                val route: Route.ExerciseDetail = backStackEntry.toRoute()
                ExerciseDetailScreen(
                    name = route.name,
                    bodyPart = route.bodyPart,
                    gifUrl = route.gifUrl,
                    instructions = route.instructions,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onStartSession = { navigationActions.navigateToActiveSession(route.name) }
                )
            }
            composable<Route.ActiveSession> { backStackEntry ->
                val route: Route.ActiveSession = backStackEntry.toRoute()
                val viewModel = koinViewModel<ActiveSessionViewModel>()
                ActiveSessionScreen(
                    exerciseName = route.exerciseName,
                    challengeId = route.challengeId,
                    challengeDay = route.challengeDay,
                    viewModel = viewModel,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onSessionFinished = {
                        if (route.challengeId != null) {
                            navController.popBackStack<Route.ChallengeDetail>(inclusive = false)
                        } else {
                            navigationActions.navigateBack()
                        }
                    }
                )
            }
            composable<Route.ChallengeDetail> { backStackEntry ->
                val route: Route.ChallengeDetail = backStackEntry.toRoute()
                com.example.fitgen.presentation.screens.challenge.ChallengeDetailScreen(
                    challengeId = route.challengeId,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToDay = { id, day -> navigationActions.navigateToChallengeDay(id, day) }
                )
            }
            composable<Route.ChallengeDay> { backStackEntry ->
                val route: Route.ChallengeDay = backStackEntry.toRoute()
                com.example.fitgen.presentation.screens.challenge.ChallengeDayScreen(
                    challengeId = route.challengeId,
                    day = route.day,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onStartSession = { id, day -> 
                        // Start session by passing a generic dummy or using first exercise name
                        // Since ActiveSession needs an exerciseName to start, we can pass a special marker like "CHALLENGE" 
                        // and handle it inside ActiveSessionScreen, or just "Challenge Day"
                        navigationActions.navigateToActiveSession("Challenge Day $day", id, day) 
                    }
                )
            }
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToSplash() {
            navController.navigate(Route.Splash) { popUpTo(Route.Splash) { inclusive = true } }
        }
        override fun navigateToOnboarding() {
            navController.navigate(Route.Onboarding) { popUpTo(0) }
        }
        override fun navigateToHome() {
            navController.navigate(Route.Home) { 
                popUpTo(Route.Splash) { inclusive = true } 
                popUpTo(Route.Onboarding) { inclusive = true }
            }
        }
        override fun navigateToDashboard() {
            navController.navigate(Route.Dashboard) { popUpTo(Route.Dashboard) { inclusive = true } }
        }
        override fun navigateToAIAssistant(initialText: String?) =
            navController.navigate(Route.AIAssistant(initialText))
        override fun navigateToDynamicWorkout() = navController.navigate(Route.DynamicWorkout)
        override fun navigateToWorkoutList() {
            navController.navigate(Route.WorkoutList) { popUpTo(Route.WorkoutList) { inclusive = true } }
        }
        override fun navigateToAddWorkout() = navController.navigate(Route.AddWorkout)
        override fun navigateToNutrition() {
            navController.navigate(Route.Nutrition) { popUpTo(Route.Nutrition) { inclusive = true } }
        }
        override fun navigateToAddMeal() = navController.navigate(Route.AddMeal)
        override fun navigateToProfile() {
            navController.navigate(Route.Profile) { launchSingleTop = true }
        }
        override fun navigateToEditProfile() {
            navController.navigate(Route.EditProfile) { launchSingleTop = true }
        }
        override fun navigateToExerciseDetail(name: String, bodyPart: String, gifUrl: String, instructions: String) {
            navController.navigate(Route.ExerciseDetail(name, bodyPart, gifUrl, instructions))
        }
        override fun navigateToActiveSession(exerciseName: String, challengeId: String?, challengeDay: Int?) {
            navController.navigate(Route.ActiveSession(exerciseName, challengeId, challengeDay))
        }
        override fun navigateToChallengeDetail(challengeId: String) {
            navController.navigate(Route.ChallengeDetail(challengeId))
        }
        override fun navigateToChallengeDay(challengeId: String, day: Int) {
            navController.navigate(Route.ChallengeDay(challengeId, day))
        }
        override fun navigateBack() = navController.popBackStack().let {}
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = modifier.clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(26.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = color, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}