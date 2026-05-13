package com.example.fitgen.presentation.screens.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitgen.domain.model.Exercise
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddWorkoutViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Navigasi kembali setelah berhasil simpan
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) onNavigateBack()
    }

    // Tampilkan error via Snackbar
    LaunchedEffect(uiState.saveError) {
        uiState.saveError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSaveError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tambah Sesi Latihan",
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
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Section: Catatan sesi ──
            SectionCard(title = "📝 Catatan Sesi (opsional)") {
                OutlinedTextField(
                    value = uiState.catatan,
                    onValueChange = viewModel::onCatatanChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Contoh: Fokus upper body hari ini") },
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    )
                )
            }

            // ── Section: Daftar gerakan yang sudah ditambahkan ──
            AnimatedVisibility(visible = uiState.gerakanList.isNotEmpty()) {
                SectionCard(title = "🏋️ Daftar Gerakan (${uiState.gerakanList.size})") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        uiState.gerakanList.forEachIndexed { index, exercise ->
                            ExerciseListItem(
                                exercise = exercise,
                                index = index,
                                onRemove = { viewModel.removeExercise(index) }
                            )
                        }
                    }
                }
            }

            // ── Section: Form tambah gerakan baru ──
            SectionCard(title = "➕ Tambah Gerakan") {
                ExerciseInputForm(
                    form = uiState.exerciseForm,
                    onNamaChange = viewModel::onNamaChange,
                    onSetsChange = viewModel::onSetsChange,
                    onRepsChange = viewModel::onRepsChange,
                    onBebanChange = viewModel::onBebanChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilledTonalButton(
                    onClick = viewModel::addExercise,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tambahkan Gerakan Ini")
                }
            }

            // ── Error level sesi ──
            AnimatedVisibility(
                visible = uiState.sessionError != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = uiState.sessionError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            // ── Tombol Simpan ──
            Button(
                onClick = viewModel::saveWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Sesi Latihan", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ExerciseInputForm(
    form: ExerciseFormState,
    onNamaChange: (String) -> Unit,
    onSetsChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onBebanChange: (String) -> Unit
) {
    // Nama gerakan (full width)
    OutlinedTextField(
        value = form.nama,
        onValueChange = onNamaChange,
        label = { Text("Nama Gerakan *") },
        placeholder = { Text("Contoh: Push Up, Squat, Deadlift") },
        isError = form.namaError != null,
        supportingText = form.namaError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        )
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Sets | Reps (berdampingan)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = form.sets,
            onValueChange = onSetsChange,
            label = { Text("Sets *") },
            placeholder = { Text("3") },
            isError = form.setsError != null,
            supportingText = form.setsError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = form.reps,
            onValueChange = onRepsChange,
            label = { Text("Reps *") },
            placeholder = { Text("12") },
            isError = form.repsError != null,
            supportingText = form.repsError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Beban (full width, opsional)
    OutlinedTextField(
        value = form.beban,
        onValueChange = onBebanChange,
        label = { Text("Beban (kg)") },
        placeholder = { Text("0 — kosongkan jika tanpa beban") },
        isError = form.bebanError != null,
        supportingText = when {
            form.bebanError != null -> { { Text(form.bebanError, color = MaterialTheme.colorScheme.error) } }
            else -> { { Text("Opsional — isi jika menggunakan beban tambahan") } }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        )
    )
}

@Composable
private fun ExerciseListItem(
    exercise: Exercise,
    index: Int,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nomor urut
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Nama + ringkasan
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.nama,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = exercise.ringkasan,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Tombol hapus
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Hapus gerakan",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
