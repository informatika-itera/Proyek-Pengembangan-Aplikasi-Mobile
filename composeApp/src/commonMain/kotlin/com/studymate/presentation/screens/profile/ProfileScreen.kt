package com.studymate.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studymate.domain.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("👤 Profil") }) }
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                profile.name.take(1).uppercase(),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(profile.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(profile.nim, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("🔥 ${profile.currentStreak} hari berturut-turut", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(onClick = onEditClick) { Text("Edit Profil") }
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
