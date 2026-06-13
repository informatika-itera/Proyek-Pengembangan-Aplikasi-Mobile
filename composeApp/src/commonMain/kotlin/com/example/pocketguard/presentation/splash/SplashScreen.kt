package com.example.pocketguard.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import pocketguard.composeapp.generated.resources.Res
import pocketguard.composeapp.generated.resources.Logo_PocketGuard

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    // Logika Delay: Splash muncul selama 2 detik lalu pindah
    LaunchedEffect(Unit) {
        visible = true
        delay(2000) // Durasi 2 detik
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B5E20)), // Gunakan warna hijau brand Anda
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(Res.drawable.Logo_PocketGuard),
                    contentDescription = "Logo PocketGuard",
                    modifier = Modifier.size(120.dp)
                )
            }
        }
    }
}