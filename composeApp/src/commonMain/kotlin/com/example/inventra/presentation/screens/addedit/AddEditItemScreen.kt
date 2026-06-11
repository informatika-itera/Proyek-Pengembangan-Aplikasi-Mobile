package com.example.inventra.presentation.screens.addedit

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.inventra.core.localization.AppStrings
import com.example.inventra.core.util.rememberImagePickerLauncher
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.model.ItemCondition
import com.example.inventra.presentation.util.getDisplayName
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    itemId: Long? = null,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val viewModel: AddEditItemViewModel = koinViewModel { parametersOf(itemId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val strings = AppStrings.current

    var showImageSourceOptions by remember { mutableStateOf(false) }
    // Image picker dari galeri HP — harus di level Composable, bukan di dalam onClick
    val imagePicker = rememberImagePickerLauncher { bytes, fileName ->
        viewModel.uploadAndSaveImage(bytes, fileName)
    }

    if (showImageSourceOptions) {
        AlertDialog(
            onDismissRequest = { showImageSourceOptions = false },
            title = { Text(strings.choosePhotoSource) },
            text = { Text(strings.choosePhotoSourceDesc) },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceOptions = false
                    imagePicker.takePhoto()
                }) {
                    Text(strings.camera)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceOptions = false
                    imagePicker.pickImage()
                }) {
                    Text(strings.gallery)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (itemId != null) strings.editProfile else strings.registerNewItem,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, strings.cancel)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    focusManager.clearFocus()
                    viewModel.saveItem(onSuccess = onSaveSuccess) 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Save, strings.save)
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error
            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Nama Barang
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(strings.itemName + " *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Deskripsi
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(strings.description) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default
                )
            )

            // Lokasi
            OutlinedTextField(
                value = uiState.location,
                onValueChange = viewModel::onLocationChange,
                label = { Text(strings.location) },
                placeholder = { Text("Nama Lokasi https://maps.google...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Default
                )
            )

            // PIC
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.picName,
                    onValueChange = viewModel::onPicNameChange,
                    label = { Text(strings.pic) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.picPhone,
                    onValueChange = viewModel::onPicPhoneChange,
                    label = { Text(strings.picPhone) },
                    placeholder = { Text("08xx") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                    )
                )
            }

            // ── Foto Barang ──────────────────────────────────────
            Text(
                strings.addPhotoTitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(enabled = !uiState.isSaving) { showImageSourceOptions = true },
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isSaving && uiState.imageUrl.isBlank() -> {
                        // Sedang upload
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                strings.uploadingPhoto,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    uiState.imageUrl.isNotBlank() -> {
                        // Foto sudah ada — tampilkan preview
                        AsyncImage(
                            model = uiState.imageUrl,
                            contentDescription = "Foto barang",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Overlay tombol ganti
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Edit,
                                    null,
                                    tint = androidx.compose.ui.graphics.Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    strings.changePhoto,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                        }
                    }
                    else -> {
                        // Belum ada foto
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                strings.addPhotoDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Text(
                                strings.photoSupportedFormats,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Hapus foto jika sudah ada
            if (uiState.imageUrl.isNotBlank() && !uiState.isSaving) {
                TextButton(
                    onClick = { viewModel.onImageUrlChange("") },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(strings.deletePhoto)
                }
            }

            // Stok
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.totalStock,
                    onValueChange = viewModel::onTotalStockChange,
                    label = { Text(strings.totalStock) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.availableStock,
                    onValueChange = viewModel::onAvailableStockChange,
                    label = { Text(strings.availableStock) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            CategoryDropdown(selected = uiState.category, onSelected = viewModel::onCategoryChange, strings = strings)
            ConditionDropdown(selected = uiState.condition, onSelected = viewModel::onConditionChange, strings = strings)

            Spacer(Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(selected: ItemCategory, onSelected: (ItemCategory) -> Unit, strings: com.example.inventra.core.localization.Strings) {
    var expanded by remember { mutableStateOf(false) }
    val categories = ItemCategory.entries.filter { it != ItemCategory.ALL }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.getDisplayName(strings), 
            onValueChange = {}, readOnly = true,
            label = { Text(strings.category) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { cat ->
                DropdownMenuItem(
                    text = { 
                        Text(cat.getDisplayName(strings))
                    },
                    onClick = { onSelected(cat); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConditionDropdown(selected: ItemCondition, onSelected: (ItemCondition) -> Unit, strings: com.example.inventra.core.localization.Strings) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.getDisplayName(strings), onValueChange = {}, readOnly = true,
            label = { Text(strings.condition) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ItemCondition.entries.forEach { cond ->
                DropdownMenuItem(
                    text = { Text(cond.getDisplayName(strings)) },
                    onClick = { onSelected(cond); expanded = false }
                )
            }
        }
    }
}