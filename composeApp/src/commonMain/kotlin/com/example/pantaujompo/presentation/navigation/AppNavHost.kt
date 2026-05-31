package com.example.pantaujompo.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

// IMPORT KOIN DAN VIEWMODEL
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.presentation.screens.home.DashboardViewModel
import com.example.pantaujompo.presentation.screens.profil.ProfilViewModel
import com.example.pantaujompo.presentation.screens.riwayat.RiwayatViewModel

// IMPORT SEMUA LAYAR
import com.example.pantaujompo.presentation.screens.home.DashboardScreen
import com.example.pantaujompo.presentation.screens.pemindai.PemindaiScreen
import com.example.pantaujompo.presentation.screens.riwayat.RiwayatScreen
import com.example.pantaujompo.presentation.screens.artikel.ArtikelScreen
import com.example.pantaujompo.presentation.screens.profil.ProfilScreen
import com.example.pantaujompo.presentation.screens.addedit.AddEditActivityScreen
import com.example.pantaujompo.presentation.screens.tracking.TrackingScreen
import com.example.pantaujompo.presentation.screens.tracking.SaveActivityScreen
import com.example.pantaujompo.presentation.screens.splash.SplashScreen
import com.example.pantaujompo.presentation.screens.profile.ProfileSetupScreen
import com.example.pantaujompo.presentation.screens.ai.AiChatScreen
import com.example.pantaujompo.presentation.theme.DarkBackground
import com.example.pantaujompo.core.util.AppStrings

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val userPreferences: UserPreferences = koinInject()
    val language by userPreferences.language.collectAsState(initial = "id")

    val bottomNavItems = listOf(
        BottomNavItem(AppStrings.get("beranda", language), Route.Beranda, Icons.Default.Home),
        BottomNavItem(AppStrings.get("nutrisi", language), Route.Pemindai, Icons.Default.Restaurant), // Ikon garpu & pisau sesuai permintaan
        BottomNavItem(AppStrings.get("statistik", language), Route.Riwayat, Icons.Default.BarChart), // Ikon statistik sesuai permintaan
        BottomNavItem(AppStrings.get("artikel", language), Route.Artikel, Icons.Default.Article),
        BottomNavItem(AppStrings.get("profil", language), Route.Profil, Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var isArticleDetailOpen by remember { mutableStateOf(false) }

    val showBottomBar = (currentDestination?.hierarchy?.any {
        it.route?.contains("AddEditActivity") == true ||
        it.route?.contains("ActivityDetail") == true ||
        it.route?.contains("DetailRiwayat") == true ||
        it.route?.contains("Tracking") == true ||
        it.route?.contains("SaveActivity") == true ||
        it.route?.contains("Splash") == true ||
        it.route?.contains("ProfileSetup") == true ||
        it.route?.contains("AiChat") == true ||
        it.route?.contains("Settings") == true ||
        it.route?.contains("About") == true
    } != true) && !isArticleDetailOpen

    val sharedDashboardViewModel: DashboardViewModel = koinViewModel()
    val profilViewModel: ProfilViewModel = koinViewModel()
    val riwayatViewModel: RiwayatViewModel = koinViewModel()

    val hasCompletedProfile by userPreferences.hasCompletedProfile.collectAsState(initial = null)

    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val backgroundColor = if (isDark) DarkBackground else Color(0xFFF0F2F5)
    
    // Glassmorphism effect colors for Bottom Bar
    val glassBgGradient = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF1E1E1E).copy(alpha = 0.85f), Color(0xFF0A0A0A).copy(alpha = 0.95f)))
    } else {
        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.85f), Color(0xFFF5F7FA).copy(alpha = 0.95f)))
    }
    
    val glassBorder = if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFBDBDBD).copy(alpha = 0.5f)
    val accentColor = MaterialTheme.colorScheme.primary
    
    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = androidx.compose.animation.fadeIn(animationSpec = tween(300, delayMillis = 300)) + androidx.compose.animation.slideInVertically(initialOffsetY = { it }, animationSpec = tween(300, delayMillis = 300)),
                exit = androidx.compose.animation.fadeOut(animationSpec = tween(300)) + androidx.compose.animation.slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp) // Slightly thinner
                            .clip(RoundedCornerShape(35.dp))
                            .background(brush = glassBgGradient)
                            .border(1.dp, glassBorder, RoundedCornerShape(35.dp))
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.route?.substringBefore("?") == item.route::class.qualifiedName
                            } == true

                            val itemBgColor = if (selected) accentColor.copy(alpha=0.15f) else Color.Transparent
                            val iconTint = if (selected) accentColor else if (isDark) Color(0xFF888888) else Color(0xFF666666)

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(itemBgColor)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = iconTint,
                                    modifier = Modifier.size(if (selected) 28.dp else 24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Splash,
            modifier = modifier, // Biarkan konten memanjang hingga ke bawah layar (di bawah navbar)
            enterTransition = { 
                scaleIn(initialScale = 0.95f, animationSpec = tween(400, easing = FastOutSlowInEasing)) + 
                fadeIn(animationSpec = tween(400)) 
            },
            exitTransition = { 
                scaleOut(targetScale = 1.05f, animationSpec = tween(400, easing = FastOutSlowInEasing)) + 
                fadeOut(animationSpec = tween(400)) 
            },
            popEnterTransition = { 
                scaleIn(initialScale = 1.05f, animationSpec = tween(400, easing = FastOutSlowInEasing)) + 
                fadeIn(animationSpec = tween(400)) 
            },
            popExitTransition = { 
                scaleOut(targetScale = 0.95f, animationSpec = tween(400, easing = FastOutSlowInEasing)) + 
                fadeOut(animationSpec = tween(400)) 
            }
        ) {
            // ==========================================
            // SUB-NAVIGASI: SPLASH & ONBOARDING
            // Layar awal saat aplikasi baru dibuka
            // ==========================================
            composable<Route.Splash> {
                SplashScreen(
                    hasCompletedProfile = hasCompletedProfile,
                    onNavigateToHome = {
                        navController.navigate(Route.Beranda) { popUpTo(Route.Splash) { inclusive = true } }
                    },
                    onNavigateToProfileSetup = {
                        navController.navigate(Route.ProfileSetup) { popUpTo(Route.Splash) { inclusive = true } }
                    }
                )
            }

            composable<Route.ProfileSetup> {
                ProfileSetupScreen(
                    onSaveClick = { name, age, weight, height, gender ->
                        profilViewModel.nama = name
                        profilViewModel.usia = age.toString()
                        profilViewModel.beratKg = weight.toString()
                        profilViewModel.tinggiCm = height.toString()
                        profilViewModel.gender = gender
                        profilViewModel.saveProfile()
                        navController.navigate(Route.Beranda) { popUpTo(Route.ProfileSetup) { inclusive = true } }
                    }
                )
            }

            // ==========================================
            // NAVIGASI 5 SCREEN UTAMA (BOTTOM NAVBAR)
            // ==========================================


            composable<Route.Beranda> { 
                DashboardScreen(
                    onNavigateToTracking = { jenis -> 
                        navController.navigate(Route.Tracking(jenis = jenis)) 
                    },
                    onNavigateToRiwayat = {
                        navController.navigate(Route.Riwayat) {
                            popUpTo(navController.graph.findStartDestination().route!!) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToProfil = {
                        navController.navigate(Route.Profil) {
                            popUpTo(navController.graph.findStartDestination().route!!) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToAiChat = {
                        navController.navigate(Route.AiChat)
                    },
                    viewModel = sharedDashboardViewModel
                ) 
            }

            composable<Route.Pemindai> {
                val usia = profilViewModel.usia
                val berat = profilViewModel.beratKg
                val profilString = "Usia $usia tahun, Berat $berat kg"

                PemindaiScreen(
                    profilData = profilString,
                    viewModel = riwayatViewModel,
                    onSimpanClick = { makananEntity ->
                        riwayatViewModel.insertMakanan(makananEntity)
                    }
                )
            }

            composable<Route.Riwayat> {
                RiwayatScreen(
                    onNavigateToDetail = { id -> navController.navigate(Route.DetailRiwayat(id.toLong())) }
                )
            }

            // ==========================================
            // SUB-NAVIGASI: FITUR TAMBAHAN & DETAIL
            // ==========================================


            composable<Route.DetailRiwayat> { backStackEntry ->
                val route: Route.DetailRiwayat = backStackEntry.toRoute()
                val riwayatList by riwayatViewModel.riwayatLariState.collectAsState()
                val activity = riwayatList.find { it.id.toLong() == route.id }
                
                if (activity != null) {
                    com.example.pantaujompo.presentation.screens.riwayat.DetailRiwayatScreen(
                        riwayat = activity,
                        onBackClick = { navController.popBackStack() },
                        onDeleteClick = {
                            riwayatViewModel.deleteActivity(activity.id)
                            navController.popBackStack()
                        }
                    )
                }
            }

             composable<Route.Artikel> { 
                ArtikelScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onArticleDetailToggled = { isOpen -> isArticleDetailOpen = isOpen }
                ) 
            }
            
            composable<Route.Profil> { 
                ProfilScreen(onNavigateToSettings = { navController.navigate(Route.Settings) }) 
            }

            composable<Route.Settings> {
                com.example.pantaujompo.presentation.screens.settings.SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAbout = { navController.navigate(Route.About) }
                )
            }

            composable<Route.About> {
                com.example.pantaujompo.presentation.screens.settings.AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<Route.AiChat> {
                AiChatScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<Route.AddEditActivity> { backStackEntry ->
                val route: Route.AddEditActivity = backStackEntry.toRoute()
                AddEditActivityScreen(activityId = route.id, onNavigateBack = { navController.popBackStack() })
            }

            composable<Route.Tracking> { backStackEntry ->
                val route: Route.Tracking = backStackEntry.toRoute()
                TrackingScreen(
                    jenis = route.jenis,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSave = { jenis, jarak, kalori, durasiMenit, pace ->
                        navController.navigate(Route.SaveActivity(jenis, jarak.toFloat(), kalori, durasiMenit, pace)) {
                            popUpTo(Route.Beranda) { inclusive = false }
                        }
                    }
                )
            }

            composable<Route.SaveActivity> { backStackEntry ->
                val route: Route.SaveActivity = backStackEntry.toRoute()
                SaveActivityScreen(
                    jenis = route.jenis,
                    jarak = route.jarak.toDouble(),
                    kalori = route.kalori,
                    durasi = route.durasi,
                    pace = route.pace,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveClick = { judul, deskripsi, jns, jrk, klr, drs, pc, ruteString, photoUri ->
                        // Saat ini simulasi save dengan memanggil VM lalu balik ke beranda
                        sharedDashboardViewModel.simpanAktivitas(judul, deskripsi, jns, jrk, klr, drs, pc, ruteString, photoUri)
                        navController.navigate(Route.Beranda) {
                            popUpTo(Route.Beranda) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

data class BottomNavItem(val title: String, val route: Route, val icon: ImageVector)