package com.example.neurodeck.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.neurodeck.presentation.screens.about.AboutScreen
import com.example.neurodeck.presentation.screens.addcard.AddCardScreen
import com.example.neurodeck.presentation.screens.aichat.AIChatScreen
import com.example.neurodeck.presentation.screens.cardlist.CardListScreen
import com.example.neurodeck.presentation.screens.createdeck.CreateDeckScreen
import com.example.neurodeck.presentation.screens.decklibrary.DeckLibraryScreen
import com.example.neurodeck.presentation.screens.editcard.EditCardScreen
import com.example.neurodeck.presentation.screens.editprofile.EditProfileScreen
import com.example.neurodeck.presentation.screens.home.HomeScreen
import com.example.neurodeck.presentation.screens.importgenerate.ImportGenerateScreen
import com.example.neurodeck.presentation.screens.profile.ProfileScreen
import com.example.neurodeck.presentation.screens.stats.StatsScreen
import com.example.neurodeck.presentation.screens.studysession.StudySessionScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { scope.launch { drawerState.close() } }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isMainTab = currentRoute in mainRoutes
    val showChrome = isMainTab

    // ROOT WRAPPER: ModalNavigationDrawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isMainTab,
        drawerContent = {
            AppDrawer(
                navController = navController,
                closeDrawer = closeDrawer,
            )
        },
    ) {

        // SCAFFOLD
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                // TopBar (dengan hamburger) muncul di semua main tab — konsisten.
                if (showChrome) {
                    AppTopBar(
                        title = resolveTopBarTitle(currentRoute),
                        canNavigateBack = false,
                        onNavigationClick = openDrawer,
                    )
                }
            },
            bottomBar = {
                if (showChrome) {
                    BottomNavBar(navController = navController)
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                // MAIN TABS

                // HOME TAB
                composable(route = Screen.Home.route) {
                    HomeScreen(
                        onCreateDeck = {
                            navController.navigate(Screen.CreateDeck.route)
                        },
                        onStudyNow = {
                            navController.navigate(Screen.Decks.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onDeckClick = { deckId ->
                            navController.navigate(Screen.CardList.createRoute(deckId))
                        },
                    )
                }

                // DECKS TAB
                composable(route = Screen.Decks.route) {
                    DeckLibraryScreen(
                        onDeckClick = { deckId ->
                            navController.navigate(Screen.CardList.createRoute(deckId))
                        },
                        onCreateDeck = {
                            navController.navigate(Screen.CreateDeck.route)
                        },
                        onImportGenerate = {
                            navController.navigate(Screen.CreateDeck.route)
                        },
                    )
                }

                // AI CHAT TAB
                composable(route = Screen.AIChat.route) {
                    AIChatScreen()
                }

                // STATS TAB
                composable(route = Screen.Stats.route) {
                    StatsScreen()
                }

                // PROFILE TAb
                composable(route = Screen.Profile.route) {
                    ProfileScreen(
                        onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                        onAbout = { navController.navigate(Screen.About.route) },
                    )
                }

                // SUB-SCREENS — Hide TopBar + BottomBar (full-screen experience)

                // CARD LIST — sudah ada (existing screen)
                composable(
                    route = Screen.CardList.route,
                    arguments = listOf(
                        navArgument(NavArgs.DECK_ID) { type = NavType.LongType },
                    ),
                ) { backStackEntry ->
                    val deckId = backStackEntry.arguments?.getLong(NavArgs.DECK_ID) ?: 0L
                    CardListScreen(
                        deckId = deckId,
                        onAddCard = { id -> navController.navigate(Screen.AddCard.createRoute(id)) },
                        onEditCard = { cardId -> navController.navigate(Screen.EditCard.createRoute(cardId)) },
                        onStartStudy = { id -> navController.navigate(Screen.StudySession.createRoute(id)) },
                        onBack = { navController.popBackStack() },
                    )
                }

                // ADD CARD
                composable(
                    route = Screen.AddCard.route,
                    arguments = listOf(
                        navArgument(NavArgs.DECK_ID) { type = NavType.LongType },
                    ),
                ) { backStackEntry ->
                    val deckId = backStackEntry.arguments?.getLong(NavArgs.DECK_ID) ?: 0L
                    AddCardScreen(
                        deckId = deckId,
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() },
                    )
                }

                // EDIT CARD
                composable(
                    route = Screen.EditCard.route,
                    arguments = listOf(
                        navArgument(NavArgs.CARD_ID) { type = NavType.LongType },
                    ),
                ) { backStackEntry ->
                    val cardId = backStackEntry.arguments?.getLong(NavArgs.CARD_ID) ?: 0L
                    EditCardScreen(
                        cardId = cardId,
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() },
                    )
                }

                // STUDY SESSION
                composable(
                    route = Screen.StudySession.route,
                    arguments = listOf(
                        navArgument(NavArgs.DECK_ID) { type = NavType.LongType },
                    ),
                ) { backStackEntry ->
                    val deckId = backStackEntry.arguments?.getLong(NavArgs.DECK_ID) ?: 0L
                    StudySessionScreen(
                        deckId = deckId,
                        onExit = { navController.popBackStack() },
                    )
                }

                 // IMPORT GENERATE (AI flashcard generation)
                composable(
                    route = Screen.ImportGenerate.route,
                    arguments = listOf(
                        navArgument(NavArgs.DECK_ID) { type = NavType.LongType },
                    ),
                ) { backStackEntry ->
                    val deckId = backStackEntry.arguments?.getLong(NavArgs.DECK_ID) ?: 0L
                    ImportGenerateScreen(
                        deckId = deckId,
                        onBack = { navController.popBackStack() },
                        onCompleted = { savedDeckId, savedCount ->
                            navController.navigate(Screen.CardList.createRoute(savedDeckId)) {
                                popUpTo(Screen.ImportGenerate.route) { inclusive = true }
                            }
                        },
                    )
                }

                composable(route = Screen.CreateDeck.route) {
                    CreateDeckScreen(
                        onBack = { navController.popBackStack() },
                        onSavedManual = { deckId ->
                            navController.navigate(Screen.CardList.createRoute(deckId)) {
                                popUpTo(Screen.CreateDeck.route) { inclusive = true }
                            }
                        },
                        onSavedAIGenerate = { deckId ->
                            // AI Generate
                            navController.navigate(Screen.ImportGenerate.createRoute(deckId)) {
                                popUpTo(Screen.CreateDeck.route) { inclusive = true }
                            }
                        },
                    )
                }

                // EDIT PROFILE
                composable(route = Screen.EditProfile.route) {
                    EditProfileScreen(
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() },
                    )
                }

                // SETTINGS
                composable(route = Screen.Settings.route) {
                    PlaceholderSubScreen(
                        title = "Pengaturan",
                        description = "Pengaturan aplikasi (theme, notifications, dll).\n(Akan dibuat di Sprint 3 polish)",
                        onBack = { navController.popBackStack() },
                    )
                }

                // ABOUT
                composable(route = Screen.About.route) {
                    AboutScreen(
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

// PLACEHOLDER COMPOSABLES

@Composable
private fun PlaceholderTabScreen(
    emoji: String,
    title: String,
    description: String,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.displayLarge,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderSubScreen(
    title: String,
    description: String,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                canNavigateBack = true,
                onNavigationClick = onBack,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(24.dp),
            ) {
                Text(
                    text = "🚧",
                    style = MaterialTheme.typography.displayMedium,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
