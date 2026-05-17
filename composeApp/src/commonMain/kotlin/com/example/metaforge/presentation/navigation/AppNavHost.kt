package com.example.metaforge.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import com.example.metaforge.presentation.screens.home.HomeScreen
import com.example.metaforge.presentation.screens.draft.DraftSetupScreen
import com.example.metaforge.presentation.screens.draft.DraftScreen
import com.example.metaforge.presentation.screens.draft.DraftViewModel
import com.example.metaforge.presentation.screens.heroselect.HeroSelectScreen
import com.example.metaforge.presentation.screens.heroselect.HeroListScreen
import com.example.metaforge.presentation.screens.heroselect.HeroInfoScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToDraftSetup = { navController.navigate(Route.DraftSetup) },
                onNavigateToHeroList = { navController.navigate(Route.HeroList) }
            )
        }
        composable<Route.DraftSetup> {
            DraftSetupScreen(
                onNavigateBack = { navController.popBackStack() },
                onStartDraft = { rank, party, pickPos, isFirst, role ->
                    navController.navigate(Route.DraftSimActive(rank, party, pickPos, isFirst, role))
                }
            )
        }
        composable<Route.DraftSimActive> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.DraftSimActive>()
            val draftViewModel: DraftViewModel = koinViewModel()

            LaunchedEffect(args) {
                draftViewModel.setupDraft(args.pickPosition, args.isFirstPick, args.preferredRole)
            }

            DraftScreen(
                isUserFirstPick = args.isFirstPick,
                userPickPosition = args.pickPosition, // Mengirim urutan pick user ke layar Draft
                viewModel = draftViewModel,
                onNavigateToHeroSelect = { slotIndex, isAlly, isBan ->
                    navController.navigate(Route.HeroSelect(slotIndex, isAlly, isBan))
                },
                onNavigateToCounter = { navController.navigate(Route.CounterPick) },
                onNavigateToSynergy = { navController.navigate(Route.Synergy) }
            )
        }
        composable<Route.HeroSelect> { backStackEntry ->
            val route: Route.HeroSelect = backStackEntry.toRoute()
            HeroSelectScreen(
                slotIndex = route.slotIndex, isAlly = route.isAlly, isBan = route.isBan,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.HeroList> {
            HeroListScreen(onNavigateToHeroInfo = { id, name, role -> navController.navigate(Route.HeroInfo(id, name, role)) })
        }
        composable<Route.HeroInfo> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.HeroInfo>()
            HeroInfoScreen(heroId = route.heroId, heroName = route.heroName, heroRole = route.heroRole, onNavigateBack = { navController.popBackStack() })
        }
        composable<Route.CounterPick> { Box(Modifier.fillMaxSize(), Alignment.Center) { Button({navController.popBackStack()}) { Text("Back") } } }
        composable<Route.Synergy> { Box(Modifier.fillMaxSize(), Alignment.Center) { Button({navController.popBackStack()}) { Text("Back") } } }
    }
}