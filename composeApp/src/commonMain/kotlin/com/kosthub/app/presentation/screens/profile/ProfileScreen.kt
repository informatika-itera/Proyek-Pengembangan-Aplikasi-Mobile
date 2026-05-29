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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Profile
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.OperationState
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.presentation.viewmodel.ProfileViewModel
import androidx.compose.runtime.rememberCoroutineScope
import com.kosthub.app.platform.LocationTracker
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    locationTracker: LocationTracker,
    platformContext: com.kosthub.app.platform.PlatformContext
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val operationState by profileViewModel.operationState.collectAsState()

    LaunchedEffect(operationState) {
        when (operationState) {
            is OperationState.Success -> {
                com.kosthub.app.platform.showToast(platformContext, (operationState as OperationState.Success).message)
                delay(1000)
                profileViewModel.clearOperationState()
            }
            is OperationState.Error -> {
                com.kosthub.app.platform.showToast(platformContext, (operationState as OperationState.Error).message)
                delay(1000)
                profileViewModel.clearOperationState()
            }
            else -> {}
        }
    }

    when (val state = uiState) {
        is UiState.Loading -> LoadingState()
        is UiState.Error -> ErrorState(message = state.message)
        is UiState.Empty -> EmptyState(text = "Profil belum tersedia")
        is UiState.Success -> ProfileForm(
            profile = state.data,
            operationState = operationState,
            locationTracker = locationTracker,
            onSave = { profileViewModel.saveProfile(it) }
        )
    }
}

@Composable
private fun ProfileForm(
    profile: Profile,
    operationState: OperationState,
    locationTracker: LocationTracker,
    onSave: (Profile) -> Unit
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }
    var latitude by remember { mutableStateOf(profile.latitude.toString()) }
    var longitude by remember { mutableStateOf(profile.longitude.toString()) }
    var isFetchingLocation by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        name = profile.name
        email = profile.email
        latitude = profile.latitude.toString()
        longitude = profile.longitude.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(48.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = initials(name), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.height(32.dp))
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
            TextField(
                value = latitude,
                onValueChange = { 
                    latitude = it
                    showError = false
                },
                label = { Text("Latitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = longitude,
                onValueChange = { 
                    longitude = it
                    showError = false
                },
                label = { Text("Longitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.material3.OutlinedButton(
                onClick = {
                    scope.launch {
                        isFetchingLocation = true
                        showError = false
                        try {
                            val loc = locationTracker.getCurrentLocation()
                            if (loc != null) {
                                latitude = loc.first.toString()
                                longitude = loc.second.toString()
                            } else {
                                errorMessage = "Gagal mengambil lokasi GPS. Pastikan GPS aktif dan izin diberikan."
                                showError = true
                            }
                        } catch (e: Exception) {
                            errorMessage = "Terjadi kesalahan: ${e.message}"
                            showError = true
                        } finally {
                            isFetchingLocation = false
                        }
                    }
                },
                enabled = !isFetchingLocation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isFetchingLocation) "Mengambil GPS..." else "Ambil Lokasi via GPS")
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        if (showError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Button(
            onClick = {
                val lat = latitude.trim().replace(",", ".").toDoubleOrNull()
                val lon = longitude.trim().replace(",", ".").toDoubleOrNull()

                if (name.isBlank()) {
                    errorMessage = "Nama tidak boleh kosong"
                    showError = true
                } else if (email.isBlank()) {
                    errorMessage = "Email tidak boleh kosong"
                    showError = true
                } else if (!email.contains("@")) {
                    errorMessage = "Format email tidak valid"
                    showError = true
                } else if (lat == null) {
                    errorMessage = "Format Latitude tidak valid (harus angka desimal)"
                    showError = true
                } else if (lon == null) {
                    errorMessage = "Format Longitude tidak valid (harus angka desimal)"
                    showError = true
                } else {
                    onSave(
                        profile.copy(
                            name = name.trim(),
                            email = email.trim(),
                            latitude = lat,
                            longitude = lon
                        )
                    )
                }
            },
            enabled = operationState !is OperationState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (operationState is OperationState.Loading) "Menyimpan..." else "Simpan Profil")
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
