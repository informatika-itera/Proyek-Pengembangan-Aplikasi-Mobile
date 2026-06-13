package com.example.fitgen.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitgen.presentation.components.FitGenPrimaryButton
import com.example.fitgen.presentation.components.FitGenTextField
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                viewModel.onProfileImageChange(it)
            }
        }
    )

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Profil berhasil disimpan!")
            viewModel.resetSaveSuccess()
            onNavigateBack() // Kembali otomatis setelah berhasil simpan
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Identitas Dasar",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                // Profile Image Picker
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { singleImagePicker.launch() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.profileImageBytes != null) {
                            val imageBitmap = remember(uiState.profileImageBytes) {
                                uiState.profileImageBytes?.toImageBitmap()
                            }
                            imageBitmap?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Icon(
                                Icons.Outlined.PhotoCamera,
                                contentDescription = "Upload Foto",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                FitGenTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Nama Lengkap",
                    errorMessage = uiState.nameError
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FitGenTextField(
                        value = uiState.age,
                        onValueChange = viewModel::onAgeChange,
                        label = "Usia (Tahun)",
                        modifier = Modifier.weight(1f),
                        errorMessage = uiState.ageError
                    )
                    FitGenTextField(
                        value = uiState.gender,
                        onValueChange = viewModel::onGenderChange,
                        label = "Jenis Kelamin",
                        modifier = Modifier.weight(1f),
                        errorMessage = uiState.genderError
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FitGenTextField(
                        value = uiState.height,
                        onValueChange = viewModel::onHeightChange,
                        label = "Tinggi Badan (cm)",
                        modifier = Modifier.weight(1f),
                        errorMessage = uiState.heightError
                    )
                    FitGenTextField(
                        value = uiState.weight,
                        onValueChange = viewModel::onWeightChange,
                        label = "Berat Badan (kg)",
                        modifier = Modifier.weight(1f),
                        errorMessage = uiState.weightError
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                FitGenPrimaryButton(
                    text = if (uiState.isSaving) "Menyimpan..." else "Simpan Perubahan",
                    onClick = viewModel::saveProfile,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
