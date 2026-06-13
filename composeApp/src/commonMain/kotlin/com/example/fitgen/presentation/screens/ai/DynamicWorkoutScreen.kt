package com.example.fitgen.presentation.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import androidx.compose.material.icons.filled.FitnessCenter
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicWorkoutScreen(
    onNavigateBack: () -> Unit,
    viewModel: DynamicWorkoutViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveRoutineName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DynamicWorkoutEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Workout Generator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Buat program latihan personal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header banner with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0D47A1), // Dark Blue
                                Color(0xFF1976D2)  // Blue
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Powered by Groq",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Pilih preferensimu dan biarkan AI merancang program latihan yang sempurna",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {

                // SECTION: Tujuan Latihan
                Column {
                    ChipSectionLabel(
                        title = "Tujuan Latihan",
                        subtitle = "Pilih satu tujuan utamamu"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GoalChip.entries.forEach { chip ->
                            val selected = uiState.selectedGoal == chip
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.onGoalSelect(chip) },
                                label = { Text(text = chip.label, fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }

                // SECTION: Durasi
                Column {
                    ChipSectionLabel(
                        title = "Durasi Latihan",
                        subtitle = "Berapa lama waktu yang tersedia?"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DurationChip.entries.forEach { chip ->
                            val selected = uiState.selectedDuration == chip
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.onDurationSelect(chip) },
                                label = { Text(text = chip.label, fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }

                // SECTION: Peralatan
                Column {
                    ChipSectionLabel(
                        title = "Peralatan Tersedia",
                        subtitle = "Pilih peralatan yang kamu punya (bisa lebih dari satu)"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EquipmentChip.entries.forEach { chip ->
                            val selected = uiState.selectedEquipment.contains(chip)
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.onEquipmentToggle(chip) },
                                label = { Text(text = chip.label, fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }

                // SECTION: Level
                Column {
                    ChipSectionLabel(
                        title = "Level Kebugaran",
                        subtitle = "Seberapa berpengalaman kamu?"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LevelChip.entries.forEach { chip ->
                            val selected = uiState.selectedLevel == chip
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.onLevelSelect(chip) },
                                label = { Text(text = chip.label, fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }

                // SECTION: Prompt Tambahan
                Column {
                    ChipSectionLabel(
                        title = "Catatan Tambahan",
                        subtitle = "Ceritakan kondisi spesifikmu (opsional)"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.customPrompt,
                        onValueChange = viewModel::onCustomPromptChange,
                        placeholder = {
                            Text(
                                "Contoh: sakit lutut, fokus lengan, mau bakar kalori cepat...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                // GENERATE BUTTON
                Button(
                    onClick = { viewModel.generateWorkout() },
                    enabled = uiState.canGenerate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Sedang merancang program...",
                            style = MaterialTheme.typography.titleSmall
                        )
                    } else {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Generate Program Latihan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // ERROR
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // RESULT
                AnimatedVisibility(
                    visible = uiState.result != null,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Program Latihan Kamu",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(onClick = { viewModel.clearResult() }) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reset",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            // Summary bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                SummaryTag(uiState.selectedDuration.label)
                                SummaryTag(uiState.selectedGoal.label)
                                SummaryTag(uiState.selectedLevel.label)
                            }

                            // List of workouts
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                uiState.result?.forEachIndexed { index, item ->
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            // Number indicator
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(
                                                        MaterialTheme.colorScheme.primary,
                                                        RoundedCornerShape(10.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${index + 1}",
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.englishName.uppercase(),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(Modifier.height(6.dp))
                                                Text(
                                                    text = item.description,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    lineHeight = 22.sp
                                                )
                                            }
                                        }
                                        
                                        if (index < (uiState.result?.size ?: 0) - 1) {
                                            Spacer(Modifier.height(24.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Save Button
                            Button(
                                onClick = { showSaveDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 20.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF800000),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Simpan ke Latihan Custom", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        // Save Dialog
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Simpan ke Latihan Custom") },
                text = {
                    OutlinedTextField(
                        value = saveRoutineName,
                        onValueChange = { saveRoutineName = it },
                        label = { Text("Nama Rutinitas (cth: Latihan AI Pagi)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (saveRoutineName.isNotBlank()) {
                                viewModel.saveToCustomRoutine(saveRoutineName)
                                saveRoutineName = ""
                                showSaveDialog = false
                            }
                        }
                    ) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    Button(onClick = { showSaveDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

// Helper Composables

@Composable
private fun ChipSectionLabel(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SummaryTag(label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}
