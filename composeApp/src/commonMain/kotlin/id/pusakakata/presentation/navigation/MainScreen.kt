package id.pusakakata.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import id.pusakakata.presentation.screens.favorite.FavoriteScreen
import id.pusakakata.presentation.screens.favorite.FavoriteViewModel
import id.pusakakata.presentation.screens.home.HomeScreen
import id.pusakakata.presentation.screens.home.HomeViewModel
import id.pusakakata.presentation.screens.profile.ProfileScreen
import id.pusakakata.presentation.screens.profile.ProfileViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.absoluteValue

@Composable
fun MainScreen(
    onNavigateToAddWord: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToGacha: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToCollection: () -> Unit
) {
    val items = listOf(
        BottomNavItem("Beranda", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Favorit", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        BottomNavItem("Profil", Icons.Filled.Person, Icons.Outlined.Person)
    )
    
    val pagerState = rememberPagerState(pageCount = { items.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = pagerState.currentPage == index
                    NavigationBarItem(
                        icon = {
                            AnimatedContent(
                                targetState = isSelected,
                                transitionSpec = {
                                    (fadeIn() + scaleIn(initialScale = 0.8f)) togetherWith fadeOut()
                                }
                            ) { selected ->
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        },
                        label = { Text(item.label) },
                        selected = isSelected,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 2,
                pageSpacing = 16.dp
            ) { page ->
                Box(
                    Modifier.graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue
                        
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        
                        scaleY = lerp(
                            start = 0.95f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                ) {
                    when (page) {
                        0 -> {
                            val viewModel: HomeViewModel = koinViewModel()
                            HomeScreen(
                                viewModel = viewModel,
                                onAddWord = onNavigateToAddWord,
                                onWordClick = onNavigateToDetail,
                                onNavigateToGacha = onNavigateToGacha,
                                onNavigateToSettings = onNavigateToSettings,
                                onNavigateToQuiz = onNavigateToQuiz
                            )
                        }
                        1 -> {
                            val viewModel: FavoriteViewModel = koinViewModel()
                            FavoriteScreen(
                                viewModel = viewModel,
                                onWordClick = onNavigateToDetail
                            )
                        }
                        2 -> {
                            val viewModel: ProfileViewModel = koinViewModel()
                            ProfileScreen(
                                viewModel = viewModel,
                                onNavigateToSettings = onNavigateToSettings,
                                onNavigateToCollection = onNavigateToCollection
                            )
                        }
                    }
                }
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
