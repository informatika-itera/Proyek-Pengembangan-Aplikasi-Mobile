package com.example.Roomie.presentation.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.presentation.util.AppStrings
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val categories = listOf("Gedung Kuliah", "Lab", "Kantin", "Parkir")
    var expanded by remember { mutableStateOf(false) }

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
                Text(
                    text = "Ada Kendala Fasilitas?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Bantu kami memperbaiki kampus dengan melaporkan kerusakan di sekitar Anda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Category Picker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(AppStrings.REPORT_CATEGORY, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = state.category,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Pilih Kategori...") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
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
                Text(AppStrings.REPORT_LOCATION, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = state.location,
                    onValueChange = viewModel::onLocationChange,
                    placeholder = { Text("Contoh: Ruang 101 GKU 2") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Description Input
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(AppStrings.REPORT_DESCRIPTION, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    placeholder = { Text("Jelaskan detail kerusakan secara singkat...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Urgency Picker (Modern Row)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(AppStrings.REPORT_URGENCY, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UrgencyLevel.entries.forEach { level ->
                        val isSelected = state.urgency == level
                        val levelColor = when(level) {
                            UrgencyLevel.LOW -> Color(0xFF4CAF50)
                            UrgencyLevel.MEDIUM -> Color(0xFFFFA500)
                            UrgencyLevel.HIGH -> MaterialTheme.colorScheme.error
                        }
                        
                        Surface(
                            onClick = { viewModel.onUrgencyChange(level) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) levelColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, if (isSelected) levelColor else Color.Transparent)
                        ) {
                            Box(Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = level.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) levelColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Photo Placeholder
            Surface(
                onClick = { /* Photo Picker */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AddAPhoto, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text("Tambahkan Foto Bukti", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = viewModel::submitReport,
                enabled = state.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(AppStrings.REPORT_SUBMIT, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
            }

            if (state.isSubmitted) {
                AlertDialog(
                    onDismissRequest = { /* Reset */ },
                    title = { Text(AppStrings.REPORT_SUCCESS_TITLE, fontWeight = FontWeight.Bold) },
                    text = { Text(AppStrings.REPORT_SUCCESS_MSG) },
                    confirmButton = {
                        TextButton(onClick = { /* Reset state */ }) {
                            Text("OK", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}
