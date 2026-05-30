package com.example.fitkos.presentation.screens.splash

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitkos.data.local.datastore.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    onNavigateToDashboard: () -> Unit
) {
    val userPreferences: UserPreferences = koinInject()
    val scope = rememberCoroutineScope()
    var showInput by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    
    // Perbaikan: gunakan 'initial' bukan 'initialValue' untuk collectAsState
    val savedName by userPreferences.userName.collectAsState(initial = "")

    LaunchedEffect(savedName) {
        if (savedName.isNotEmpty() && savedName != "Sobat Kos") {
            delay(1500)
            onNavigateToDashboard()
        } else {
            delay(1500)
            showInput = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            
            Text(
                text = "FitKos",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Hidup sehat di kos, mudah setiap hari.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(
                visible = showInput,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Selamat datang!\nMasukkan nama kamu untuk memulai.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Masukkan nama...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                scope.launch {
                                    userPreferences.setUserName(name)
                                    onNavigateToDashboard()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mulai")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, null)
                    }
                }
            }
        }
    }
}
