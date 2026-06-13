package com.example.fitgen.presentation.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import noteai.composeapp.generated.resources.Res
import noteai.composeapp.generated.resources.logo

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val navigateToRoute by viewModel.navigateToRoute.collectAsState()

    LaunchedEffect(navigateToRoute) {
        when (navigateToRoute) {
            "home" -> onNavigateToHome()
            "onboarding" -> onNavigateToOnboarding()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF800000)), // Warna maroon yang elegan
        contentAlignment = Alignment.Center
    ) {
        // Asumsi logo disimpan di composeResources/drawable/logo.png
        // Jika drawable belum ada, sementara ini bisa throw error compile
        // jadi pastikan User menambahkannya dengan nama file logo.png
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "FitGen Logo",
            modifier = Modifier.size(120.dp)
        )
    }
}
