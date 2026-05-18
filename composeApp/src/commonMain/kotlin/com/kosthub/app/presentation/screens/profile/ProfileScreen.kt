package com.kosthub.app.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Profile
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel) {
    val uiState by profileViewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> LoadingState()
        is UiState.Error -> ErrorState(message = state.message)
        is UiState.Empty -> EmptyState(text = "Profil belum tersedia")
        is UiState.Success -> ProfileForm(
            profile = state.data,
            onSave = { profileViewModel.saveProfile(it) }
        )
    }
}

@Composable
private fun ProfileForm(profile: Profile, onSave: (Profile) -> Unit) {
    var name by remember { mutableStateOf(profile.name) }
    var role by remember { mutableStateOf(profile.role) }
    var contact by remember { mutableStateOf(profile.contact) }
    var preference by remember { mutableStateOf(profile.preference) }
    var location by remember { mutableStateOf(profile.location) }

    LaunchedEffect(profile) {
        name = profile.name
        role = profile.role
        contact = profile.contact
        preference = profile.preference
        location = profile.location
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(48.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = initials(name), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Nama") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = role,
                onValueChange = { role = it },
                label = { Text(text = "Peran") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text(text = "Kontak") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = preference,
                onValueChange = { preference = it },
                label = { Text(text = "Preferensi") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(text = "Lokasi Manual") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            onSave(
                profile.copy(
                    name = name,
                    role = role,
                    contact = contact,
                    preference = preference,
                    location = location
                )
            )
        }) {
            Text(text = "Simpan Profil")
        }
    }
}

private fun initials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}
