package com.example.mapenumkm.presentation.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import mapenumkm.composeapp.generated.resources.Res
import mapenumkm.composeapp.generated.resources.logo_mapen
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(2000) // Tampilkan splash selama 2 detik
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(Res.drawable.logo_mapen),
                        contentDescription = "MaPen UMKM Logo",
                        modifier = Modifier
                            .size(240.dp)
                            .padding(bottom = 24.dp),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = "MaPen UMKM",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF15803D), // Dark Green
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Manajemen Penjualan UMKM",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF1C1B1F),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Bottom tagline
        Text(
            text = "Aplikasi pintar untuk kelola penjualan UMKM",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF49454F),
                fontSize = 14.sp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            textAlign = TextAlign.Center
        )
    }
}
