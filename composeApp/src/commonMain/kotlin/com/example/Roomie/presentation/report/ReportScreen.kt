package com.example.Roomie.presentation.report

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.presentation.util.AppStrings
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val categories = listOf("Gedung Kuliah", "Lab", "Kantin", "Parkir")
    var expanded by remember { mutableStateOf(false) }

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                viewModel.onImagePicked(it)
            }
        }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(AppStrings.REPORT_TITLE, fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Intro Text
            Column {
                Text("Lapor Fasilitas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(AppStrings.REPORT_SUBTITLE, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Category Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(AppStrings.REPORT_CATEGORY, fontWeight = FontWeight.Bold)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = state.category,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Pilih Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    viewModel.onCategoryChange(category)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Location Input
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(AppStrings.REPORT_LOCATION, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = state.location,
                    onValueChange = viewModel::onLocationChange,
                    placeholder = { Text("Contoh: GKU 2 - Ruang 101") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Description Input
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(AppStrings.REPORT_DESC, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    placeholder = { Text("Jelaskan detail kerusakan...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Photo Evidence (RICH REPORTING)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Bukti Foto", fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { singleImagePicker.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.selectedImage != null) {
                        Image(
                            bitmap = state.selectedImage!!.toImageBitmap(),
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable { viewModel.onImagePicked(null) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Text("Tambah Foto Bukti", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Urgency Level
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(AppStrings.REPORT_URGENCY, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    UrgencyOption("RENDAH", UrgencyLevel.LOW, state.urgency, viewModel::onUrgencyChange, Color(0xFF4CAF50))
                    UrgencyOption("SEDANG", UrgencyLevel.MEDIUM, state.urgency, viewModel::onUrgencyChange, Color(0xFFFFA500))
                    UrgencyOption("TINGGI", UrgencyLevel.HIGH, state.urgency, viewModel::onUrgencyChange, Color(0xFFB22222))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = viewModel::submitReport,
                enabled = state.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(AppStrings.REPORT_SUBMIT, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
            }

            if (state.isSubmitted) {
                AlertDialog(
                    onDismissRequest = { 
                        viewModel.resetState()
                    },
                    title = { Text(AppStrings.REPORT_SUCCESS_TITLE, fontWeight = FontWeight.Bold) },
                    text = { Text(AppStrings.REPORT_SUCCESS_MSG) },
                    confirmButton = {
                        TextButton(onClick = { 
                            viewModel.resetState()
                        }) {
                            Text("OK", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun UrgencyOption(
    label: String,
    level: UrgencyLevel,
    current: UrgencyLevel,
    onSelect: (UrgencyLevel) -> Unit,
    color: Color
) {
    val selected = level == current
    Surface(
        modifier = Modifier
            .width(100.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSelect(level) },
        color = if (selected) color else color.copy(alpha = 0.1f),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label, 
                style = MaterialTheme.typography.labelSmall, 
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else color
            )
        }
    }
}
