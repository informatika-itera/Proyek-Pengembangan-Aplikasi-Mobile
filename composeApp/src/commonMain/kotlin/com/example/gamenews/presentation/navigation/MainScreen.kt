package com.example.gamenews.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gamenews.domain.model.Game
import com.example.gamenews.presentation.screens.ai.AIRecommendationSheet
import com.example.gamenews.presentation.screens.home.HomeScreen
import com.example.gamenews.presentation.screens.home.HomeViewModel
import com.example.gamenews.presentation.screens.wishlist.WishlistScreen
import com.example.gamenews.presentation.screens.wishlist.WishlistViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit
) {
    var currentTab by remember { mutableStateOf(0) }
    val primaryColor = Color(0xFF6750A4)

    val wishlistViewModel: WishlistViewModel = koinViewModel()
    val wishlistGames by wishlistViewModel.wishlistGames.collectAsState()

    val homeViewModel: HomeViewModel = koinViewModel()
    val availableGenres by homeViewModel.availableGenres.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var sheetContent by remember { mutableStateOf<SheetContent>(SheetContent.Menu) }
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            AIRecommendationSheet(
                content = sheetContent,
                wishlistGames = wishlistGames,
                availableGenres = availableGenres,
                onSelectWishlist = { sheetContent = SheetContent.WishlistResult(wishlistGames) },
                onSelectGenre = { sheetContent = SheetContent.GenreInput("") },
                onSelectCustom = { sheetContent = SheetContent.CustomInput("") },
                onGenreChange = { genre ->
                    if (sheetContent is SheetContent.GenreInput) {
                        sheetContent = SheetContent.GenreInput(genre)
                    }
                },
                onCustomInputChange = { text ->
                    if (sheetContent is SheetContent.CustomInput) {
                        sheetContent = SheetContent.CustomInput(text)
                    }
                },
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        sheetContent = SheetContent.Menu
                    }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                if (currentTab == 0 || currentTab == 1) {
                    FloatingActionButton(
                        onClick = {
                            sheetContent = SheetContent.Menu
                            scope.launch { sheetState.show() }
                        },
                        backgroundColor = primaryColor,
                        contentColor = Color.White
                    ) {
                        Text("✨", style = MaterialTheme.typography.h6)
                    }
                }
            },
            bottomBar = {
                BottomNavigation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    backgroundColor = primaryColor,
                    contentColor = Color.White
                ) {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Wishlist") },
                        label = { Text("Wishlist") },
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    0 -> HomeScreen(
                        onNavigateToDetail = onNavigateToDetail,
                        onNavigateToAI = onNavigateToAI,
                        viewModel = homeViewModel
                    )
                    1 -> WishlistScreen(onNavigateToDetail = onNavigateToDetail)
                    2 -> SettingPlaceholderScreen(primaryColor = primaryColor)
                }
            }
        }
    }
}

sealed interface SheetContent {
    data object Menu : SheetContent
    data class GenreInput(val genre: String) : SheetContent
    data class WishlistResult(val games: List<Game>) : SheetContent
    data class CustomInput(val text: String) : SheetContent
}

@Composable
fun SettingPlaceholderScreen(primaryColor: Color) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                backgroundColor = primaryColor,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}