package com.example.noteai.presentation.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.noteai.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToAuth: () -> Unit,
    userRepository: UserRepository = koinInject()
) {
    LaunchedEffect(Unit) {
        // Jalankan pengecekan login secara paralel dengan delay visual
        val user = userRepository.getCurrentUser().firstOrNull()
        
        // Delay visual singkat biar user sempat lihat tulisan CookNote
        delay(1200) 
        
        if (user != null) {
            onNavigateToMain()
        } else {
            onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBE7)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CookNote",
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
    }
}
