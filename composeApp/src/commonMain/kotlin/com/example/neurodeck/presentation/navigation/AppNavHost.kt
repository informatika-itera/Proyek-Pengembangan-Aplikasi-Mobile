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

// ════════════════════════════════════════════════════════════════════════════
// AppNavHost.kt — commonMain
//
// Sprint 2 — Prioritas 3a.4 (Navigation Infrastructure FINALE)
//
// Root composable navigation NeuroDeck. Bertanggung jawab:
//   1. Wrap entire app dalam ModalNavigationDrawer (untuk side drawer)
//   2. Scaffold dengan TopBar (top) + BottomNavBar (bottom) + NavHost (content)
//   3. Show/hide TopBar+BottomBar berdasarkan apakah current route adalah
//      main tab (show) atau sub-screen (hide — full-screen experience untuk
//      StudySession, AddCard, dll)
//   4. Wire semua composable destinations (main tabs + sub-screens)
//   5. Provide placeholder screens untuk route yang belum di-implement
//      (akan di-replace one-by-one di P3c, P3e, P3f, P4)
//
// PATTERN: Single NavHost, Scaffold wraps NavHost (BUKAN sebaliknya).
// Ini Material 3 recommended pattern untuk bottom-nav-style apps:
//   - TopBar + BottomBar persistent across all destinations (kalau show)
//   - Content area animate antar destinasi (NavHost transitions)
//
// Alternative pattern (Scaffold per-screen) ditolak karena:
//   - Duplicate TopBar/BottomBar setup di setiap screen
//   - Transition antar tab terlihat janky (TopBar/BottomBar flash)
// ════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
) {
    // ════════════════════════════════════════════════════════════════════════
    // STATE: Drawer + scope untuk open/close drawer dari coroutine
    // ════════════════════════════════════════════════════════════════════════
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Helper lambdas — hindari `scope.launch { drawerState.xxx() }` boilerplate
    // di multiple call sites.
    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { scope.launch { drawerState.close() } }

    // ════════════════════════════════════════════════════════════════════════
    // CURRENT ROUTE — di-observe untuk:
    //   1. Decide show/hide TopBar+BottomBar
    //   2. Auto-set TopBar title via resolveTopBarTitle()
    // ════════════════════════════════════════════════════════════════════════
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isMainTab = currentRoute in mainRoutes
    // "chrome" = TopBar + BottomBar collectively. Hanya muncul di main tab,
    // sub-screen render full-screen tanpa chrome (untuk immersive experience,
    // dan supaya sub-screen bisa render TopBar custom-nya sendiri).
    val showChrome = isMainTab

    // ════════════════════════════════════════════════════════════════════════
    // ROOT WRAPPER: ModalNavigationDrawer
    // ════════════════════════════════════════════════════════════════════════
    ModalNavigationDrawer(
        drawerState = drawerState,
        // Drawer hanya bisa di-gesture-swipe di main tabs.
        // Di sub-screens (StudySession, AddCard) gesture bisa conflict dengan
        // content gestures — disable swipe, user masih bisa back via tombol.
        gesturesEnabled = isMainTab,
        drawerContent = {
            AppDrawer(
                navController = navController,
                closeDrawer = closeDrawer,
            )
        },
    ) {
        // ════════════════════════════════════════════════════════════════════
        // SCAFFOLD: TopBar (conditional) + BottomBar (conditional) + content
        // ════════════════════════════════════════════════════════════════════
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (showChrome) {
                    AppTopBar(
                        title = resolveTopBarTitle(currentRoute),
                        canNavigateBack = false,
                        onNavigationClick = openDrawer,
                    )
                }
                // else: no TopBar — sub-screens render their own (or none)
            },
            bottomBar = {
                if (showChrome) {
                    BottomNavBar(navController = navController)
                }
            },
        ) { innerPadding ->
            // NavHost content area — Scaffold provides innerPadding accounting
            // for TopBar + BottomBar height (kalau ada).
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                // ════════════════════════════════════════════════════════════
                // MAIN TABS (5) — Show TopBar + BottomBar
                // ════════════════════════════════════════════════════════════

                // 🏠 HOME TAB — Dashboard ringkas + Continue Learning
                composable(route = Screen.Home.route) {
                    HomeScreen(
                        onCreateDeck = {
                            navController.navigate(Screen.CreateDeck.route)
                        },
                        onStudyNow = {
                            // Tidak ada concept "global session" — arahkan ke
                            // Decks tab, user pilih deck untuk study. Pakai
                            // popUpTo pattern supaya konsisten dengan tab nav.
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

                // 📚 DECKS TAB — wire ke existing DeckLibraryScreen (refactored P3d)
                composable(route = Screen.Decks.route) {
                    DeckLibraryScreen(
                        onDeckClick = { deckId ->
                            navController.navigate(Screen.CardList.createRoute(deckId))
                        },
                        onCreateDeck = {
                            navController.navigate(Screen.CreateDeck.route)
                        },
                        onImportGenerate = {
                            // Quick AI generate dari Decks tab — deckId=0L
                            // berarti "buat deck baru sambil generate".
                            // Sementara CreateDeck dulu (user input nama),
                            // baru AI generate. Simpler than supporting both flows.
                            navController.navigate(Screen.CreateDeck.route)
                        },
                    )
                }

                // 💬 AI CHAT TAB — Tutor AI conversational (P3f)
                composable(route = Screen.AIChat.route) {
                    AIChatScreen()
                }

                // 📊 STATS TAB — Analytics belajar mendalam (P4)
                composable(route = Screen.Stats.route) {
                    StatsScreen()
                }

                // 👤 PROFILE TAB — User info + Settings + Data Management (P3e)
                composable(route = Screen.Profile.route) {
                    ProfileScreen(
                        onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                        onAbout = { navController.navigate(Screen.About.route) },
                    )
                }

                // ════════════════════════════════════════════════════════════
                // SUB-SCREENS — Hide TopBar + BottomBar (full-screen experience)
                // ════════════════════════════════════════════════════════════

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

                // ADD CARD — sudah ada
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

                // EDIT CARD — sudah ada
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

                // STUDY SESSION — sudah ada
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

                // ════════════════════════════════════════════════════════════
                // SUB-SCREENS YANG BELUM ADA — Placeholder dengan TopBar+back
                // ════════════════════════════════════════════════════════════

                // IMPORT GENERATE (AI flashcard generation) — REAL screen P3d.3
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
                            // Setelah selesai save, navigate ke CardList deck tersebut.
                            // popUpTo(ImportGenerate inclusive) supaya back tidak balik
                            // ke form generate (user sudah selesai).
                            navController.navigate(Screen.CardList.createRoute(savedDeckId)) {
                                popUpTo(Screen.ImportGenerate.route) { inclusive = true }
                            }
                        },
                    )
                }

                // CREATE DECK — form bikin deck baru (P3d.2)
                composable(route = Screen.CreateDeck.route) {
                    CreateDeckScreen(
                        onBack = { navController.popBackStack() },
                        onSavedManual = { deckId ->
                            // Manual = navigate ke CardList, user add cards manually via FAB.
                            // popUpTo(CreateDeck inclusive) supaya back dari CardList
                            // langsung ke Decks tab, tidak balik ke form CreateDeck.
                            navController.navigate(Screen.CardList.createRoute(deckId)) {
                                popUpTo(Screen.CreateDeck.route) { inclusive = true }
                            }
                        },
                        onSavedAIGenerate = { deckId ->
                            // AI Generate = langsung ke ImportGenerate flow.
                            navController.navigate(Screen.ImportGenerate.createRoute(deckId)) {
                                popUpTo(Screen.CreateDeck.route) { inclusive = true }
                            }
                        },
                    )
                }

                // EDIT PROFILE — real screen P3e
                composable(route = Screen.EditProfile.route) {
                    EditProfileScreen(
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() },
                    )
                }

                // SETTINGS — placeholder (existing route)
                composable(route = Screen.Settings.route) {
                    PlaceholderSubScreen(
                        title = "Pengaturan",
                        description = "Pengaturan aplikasi (theme, notifications, dll).\n(Akan dibuat di Sprint 3 polish)",
                        onBack = { navController.popBackStack() },
                    )
                }

                // ABOUT — real screen P3e
                composable(route = Screen.About.route) {
                    AboutScreen(
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PLACEHOLDER COMPOSABLES — Visual stub untuk route yang belum di-implement
// ════════════════════════════════════════════════════════════════════════════

/**
 * Placeholder untuk MAIN TAB yang belum dibangun (Home/AIChat/Stats/Profile).
 *
 * Tidak punya TopBar sendiri karena AppNavHost sudah render TopBar untuk
 * main tabs. Hanya konten body sederhana untuk feedback visual ke user
 * (atau dosen yang demo): "tab ada, tinggal di-implement".
 */
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

/**
 * Placeholder untuk SUB-SCREEN yang belum dibangun.
 *
 * Beda dengan PlaceholderTabScreen: PUNYA TopBar sendiri dengan back arrow,
 * karena sub-screens tidak dapat chrome dari Scaffold root (showChrome = false).
 */
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