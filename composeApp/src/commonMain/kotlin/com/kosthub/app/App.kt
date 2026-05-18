package com.kosthub.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kosthub.app.data.local.DatabaseDriverFactory
import com.kosthub.app.data.local.KostDatabaseFactory
import com.kosthub.app.data.repository.KostRepositoryImpl
import com.kosthub.app.data.repository.ProfileRepositoryImpl
import com.kosthub.app.platform.PlatformContext
import com.kosthub.app.presentation.navigation.AppNavHost
import com.kosthub.app.presentation.navigation.BottomNavBar
import com.kosthub.app.presentation.viewmodel.KostViewModel
import com.kosthub.app.presentation.viewmodel.ProfileViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun App(platformContext: PlatformContext) {
    MaterialTheme {
        val navController = rememberNavController()
        val database = remember {
            val driverFactory = DatabaseDriverFactory(platformContext)
            KostDatabaseFactory(driverFactory).create()
        }
        val kostViewModel = remember { KostViewModel(KostRepositoryImpl(database)) }
        val profileViewModel = remember { ProfileViewModel(ProfileRepositoryImpl(database)) }
        val uiState by kostViewModel.uiState.collectAsState()

        DisposableEffect(Unit) {
            onDispose {
                kostViewModel.dispose()
                profileViewModel.dispose()
            }
        }

        Scaffold(
            bottomBar = { BottomNavBar(navController = navController) }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                uiState = uiState,
                viewModel = kostViewModel,
                profileViewModel = profileViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
