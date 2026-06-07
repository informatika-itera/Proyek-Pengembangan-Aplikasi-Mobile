package com.kosthub.app.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Profile
import com.kosthub.app.platform.LocationTracker
import com.kosthub.app.platform.PlatformContext
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.OperationState
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    locationTracker: LocationTracker,
    platformContext: PlatformContext
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
            platformContext = platformContext,
            onSave = { profileViewModel.saveProfile(it) }
        )
    }
}

@Composable
private fun ProfileForm(
    profile: Profile,
    operationState: OperationState,
    locationTracker: LocationTracker,
    platformContext: PlatformContext,
    onSave: (Profile) -> Unit
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }
    var latitude by remember { mutableStateOf(profile.latitude.toString()) }
    var longitude by remember { mutableStateOf(profile.longitude.toString()) }
    var isFetchingLocation by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        name = profile.name
        email = profile.email
        latitude = profile.latitude.toString()
        longitude = profile.longitude.toString()
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    )
    val fieldShape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(48.dp),
            modifier = Modifier.size(96.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initials(name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = name.ifBlank { "Pengguna" },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = email.ifBlank { "Belum ada email" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Informasi Pribadi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    shape = fieldShape,
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = fieldShape,
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lokasi Kampus",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = fieldShape,
                        colors = textFieldColors,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = fieldShape,
                        colors = textFieldColors,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isFetchingLocation = true
                            try {
                                val loc = locationTracker.getCurrentLocation()
                                if (loc != null) {
                                    latitude = loc.first.toString()
                                    longitude = loc.second.toString()
                                    com.kosthub.app.platform.showToast(platformContext, "Lokasi GPS berhasil diambil")
                                } else {
                                    com.kosthub.app.platform.showToast(platformContext, "Gagal mengambil lokasi GPS. Pastikan GPS aktif dan izin diberikan.")
                                }
                            } catch (e: Exception) {
                                com.kosthub.app.platform.showToast(platformContext, "Terjadi kesalahan: ${e.message}")
                            } finally {
                                isFetchingLocation = false
                            }
                        }
                    },
                    enabled = !isFetchingLocation,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.GpsFixed,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isFetchingLocation) "Mengambil GPS..." else "Ambil Lokasi via GPS")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val lat = latitude.trim().replace(",", ".").toDoubleOrNull()
                val lon = longitude.trim().replace(",", ".").toDoubleOrNull()
                when {
                    name.isBlank() -> com.kosthub.app.platform.showToast(platformContext, "Nama tidak boleh kosong")
                    email.isBlank() -> com.kosthub.app.platform.showToast(platformContext, "Email tidak boleh kosong")
                    !email.contains("@") -> com.kosthub.app.platform.showToast(platformContext, "Format email tidak valid")
                    lat == null -> com.kosthub.app.platform.showToast(platformContext, "Format Latitude tidak valid")
                    lon == null -> com.kosthub.app.platform.showToast(platformContext, "Format Longitude tidak valid")
                    lat < -90.0 || lat > 90.0 -> com.kosthub.app.platform.showToast(platformContext, "Latitude harus antara -90 dan 90 derajat")
                    lon < -180.0 || lon > 180.0 -> com.kosthub.app.platform.showToast(platformContext, "Longitude harus antara -180 dan 180 derajat")
                    else -> onSave(profile.copy(name = name.trim(), email = email.trim(), latitude = lat, longitude = lon))
                }
            },
            enabled = operationState !is OperationState.Loading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 88.dp)
        ) {
            Text(
                text = if (operationState is OperationState.Loading) "Menyimpan..." else "Simpan Profil",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
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
