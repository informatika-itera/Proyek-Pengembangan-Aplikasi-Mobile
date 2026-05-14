package com.example.Roomie.presentation.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        topBar = {
            TopAppBar(title = { Text(AppStrings.REPORT_TITLE) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(AppStrings.REPORT_CATEGORY) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
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

            // Location
            OutlinedTextField(
                value = state.location,
                onValueChange = viewModel::onLocationChange,
                label = { Text(AppStrings.REPORT_LOCATION) },
                placeholder = { Text("Contoh: Gedung A Lantai 2 KM Laki-laki") },
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(AppStrings.REPORT_DESCRIPTION) },
                placeholder = { Text("Ceritakan detail kerusakannya...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Photo Picker Placeholder
            Button(
                onClick = { /* Implementasi Photo Picker */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(AppStrings.REPORT_PHOTO)
            }

            // Urgency
            Text(
                text = AppStrings.REPORT_URGENCY,
                style = MaterialTheme.typography.titleSmall
            )
            Column {
                UrgencyLevel.entries.forEach { level ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = state.urgency == level,
                            onClick = { viewModel.onUrgencyChange(level) }
                        )
                        Text(
                            text = level.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Submit Button
            Button(
                onClick = viewModel::submitReport,
                enabled = state.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(AppStrings.REPORT_SUBMIT)
                }
            }

            if (state.isSubmitted) {
                AlertDialog(
                    onDismissRequest = { /* Handle dismiss */ },
                    title = { Text(AppStrings.REPORT_SUCCESS_TITLE) },
                    text = { Text(AppStrings.REPORT_SUCCESS_MSG) },
                    confirmButton = {
                        TextButton(onClick = { /* Reset state atau navigasi */ }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
