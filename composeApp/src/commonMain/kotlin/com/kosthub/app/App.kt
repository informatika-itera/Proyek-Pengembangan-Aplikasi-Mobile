package com.kosthub.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kosthub.app.data.local.DatabaseDriverFactory
import com.kosthub.app.data.local.KostDatabaseFactory
import com.kosthub.app.data.remote.api.ApiServiceImpl
import com.kosthub.app.data.remote.api.GeminiService
import com.kosthub.app.data.repository.KostRepositoryImpl
import com.kosthub.app.data.repository.ProfileRepositoryImpl
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kosthub.app.platform.PlatformContext
import com.kosthub.app.presentation.navigation.AppNavHost
import com.kosthub.app.presentation.navigation.BottomNavBar
import com.kosthub.app.presentation.viewmodel.KostViewModel
import com.kosthub.app.presentation.viewmodel.HomeViewModel
import com.kosthub.app.presentation.viewmodel.ProfileViewModel
import com.kosthub.app.presentation.viewmodel.RecommendationViewModel
import androidx.compose.runtime.collectAsState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Composable
fun App(platformContext: PlatformContext) {
    val database = remember {
        val driverFactory = DatabaseDriverFactory(platformContext)
        KostDatabaseFactory(driverFactory).create()
    }

    // Shared Ktor client for ApiService & GeminiService
    val httpClient = remember {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    val apiService = remember {
        ApiServiceImpl(client = httpClient, baseUrl = "https://kosthup-api.klikolio-creative.workers.dev")
    }

    val geminiService = remember { GeminiService(client = httpClient) }

    val repository = remember { KostRepositoryImpl(database, apiService) }
    val kostViewModel = remember { KostViewModel(repository) }
    val homeViewModel = remember { HomeViewModel(repository) }
    val profileViewModel = remember { ProfileViewModel(ProfileRepositoryImpl(database)) }
    val recommendationViewModel = remember { RecommendationViewModel(geminiService) }
    val uiState by kostViewModel.uiState.collectAsState()

    val isDarkTheme = isSystemInDarkTheme()

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        val navController = rememberNavController()

        DisposableEffect(Unit) {
            onDispose {
                kostViewModel.dispose()
                homeViewModel.dispose()
                profileViewModel.dispose()
                recommendationViewModel.dispose()
            }
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()

        Scaffold(
            bottomBar = {
                BottomNavBar(navController = navController)
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                uiState = uiState,
                viewModel = kostViewModel,
                homeViewModel = homeViewModel,
                profileViewModel = profileViewModel,
                recommendationViewModel = recommendationViewModel,
                platformContext = platformContext,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
