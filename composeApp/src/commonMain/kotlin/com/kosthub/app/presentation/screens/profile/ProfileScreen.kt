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
import com.kosthub.app.presentation.state.OperationState
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel) {
    val uiState by profileViewModel.uiState.collectAsState()
    val operationState by profileViewModel.operationState.collectAsState()

    LaunchedEffect(operationState) {
        if (operationState is OperationState.Success) {
            delay(1500)
            profileViewModel.clearOperationState()
        }
    }

    when (val state = uiState) {
        is UiState.Loading -> LoadingState()
        is UiState.Error -> ErrorState(message = state.message)
        is UiState.Empty -> EmptyState(text = "Profil belum tersedia")
        is UiState.Success -> ProfileForm(
            profile = state.data,
            operationState = operationState,
            onSave = { profileViewModel.saveProfile(it) }
        )
    }
}

@Composable
private fun ProfileForm(
    profile: Profile,
    operationState: OperationState,
    onSave: (Profile) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }

    LaunchedEffect(profile) {
        name = profile.name
        email = profile.email
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
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        when (operationState) {
            is OperationState.Loading -> Text(
                text = "Menyimpan profil...",
                style = MaterialTheme.typography.bodySmall
            )
            is OperationState.Success -> Text(
                text = operationState.message,
                style = MaterialTheme.typography.bodySmall
            )
            is OperationState.Error -> Text(
                text = operationState.message,
                style = MaterialTheme.typography.bodySmall
            )
            OperationState.Idle -> Unit
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
            onSave(
                profile.copy(
                    name = name,
                    email = email
                )
            )
        },
            enabled = operationState !is OperationState.Loading
        ) {
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
