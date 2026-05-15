package com.itera.news.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(2000L) // Tunggu 2 detik
        onNavigateToHome()
    }
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
        Text(
            text = "TubesNews - MBG", 
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        ) 
    }
}

@Composable
fun BookmarkScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Bookmark Screen") }
}

@Composable
fun AboutScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("About Screen") }
}