package com.studymate.presentation.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studymate.domain.model.UserProfile
import com.studymate.Res
import com.studymate.app_logo
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { 
            CenterAlignedTopAppBar(
                title = { Text("Profil Pengguna", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            if (isDarkTheme) Icons.Default.LightMode 
                            else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            ) 
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ProfileUiState.Empty -> OnboardingForm(onSave = { n, i -> viewModel.saveProfile(n, i) })
                is ProfileUiState.Success -> ProfileContent(profile = state.profile, onEditClick = { viewModel.enterEditMode() })
                is ProfileUiState.EditMode -> ProfileEditForm(
                    profile = state.profile,
                    onSave = { n, i -> viewModel.saveProfile(n, i) },
                    onCancel = { viewModel.cancelEdit() }
                )
            }
        }
    }
}

@Composable
private fun OnboardingForm(onSave: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Selamat Datang di StudyMate! 🐬", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Isi profil kamu untuk memulai")
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Lengkap") })
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = nim, onValueChange = { nim = it }, label = { Text("NIM") })
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onSave(name, nim) },
            enabled = name.isNotBlank() && nim.isNotBlank()
        ) { Text("Mulai Belajar") }
    }
}

@Composable
private fun ProfileContent(profile: UserProfile, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.app_logo),
                contentDescription = "Profile Avatar",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(profile.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Text(profile.nim, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Statistik Belajar", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Heatmap Placeholder
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Learning Heatmap", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(12.dp))
                // Grid of small boxes for heatmap
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(4) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(20) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                                        .background(
                                            if ((0..10).random() > 7) MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        ) { 
            Text("Edit Profil") 
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextButton(onClick = {}) {
            Text("Tentang StudyMate", color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun ProfileEditForm(profile: UserProfile, onSave: (String, String) -> Unit, onCancel: () -> Unit) {
    var name by remember { mutableStateOf(profile.name) }
    var nim by remember { mutableStateOf(profile.nim) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Lengkap") })
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = nim, onValueChange = { nim = it }, label = { Text("NIM") })
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(onClick = onCancel) { Text("Batal") }
            Button(onClick = { onSave(name, nim) }, enabled = name.isNotBlank() && nim.isNotBlank()) { Text("Simpan") }
        }
    }
}
