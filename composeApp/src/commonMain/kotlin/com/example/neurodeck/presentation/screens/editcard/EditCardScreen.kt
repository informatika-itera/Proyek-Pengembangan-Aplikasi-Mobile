package com.example.neurodeck.presentation.screens.editcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neurodeck.presentation.components.ErrorMessage
import com.example.neurodeck.presentation.components.LoadingIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCardScreen(
    cardId: Long,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EditCardViewModel = koinViewModel { parametersOf(cardId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Kartu") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(12.dp),
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            // Capture ke local val supaya null-check bisa smart-cast (hindari !! → cegah NPE).
            val errorMessage = uiState.errorMessage

            when {
                uiState.isLoading -> LoadingIndicator()

                errorMessage != null && uiState.front.isEmpty() -> {
                    ErrorMessage(message = errorMessage)
                }

                else -> EditCardForm(
                    uiState = uiState,
                    onFrontChange = viewModel::onFrontChange,
                    onBackChange = viewModel::onBackChange,
                    onSave = {
                        viewModel.saveCard(onSuccess = {
                            // Snackbar jalan paralel (tidak blok navigasi)
                            scope.launch {
                                snackbarHostState.showSnackbar("✅ Kartu berhasil diperbarui!")
                            }
                            // Navigasi balik setelah jeda singkat biar popup sempat kelihatan
                            scope.launch {
                                delay(600)
                                onSaved()
                            }
                        })
                    },
                )
            }
        }
    }
}

@Composable
private fun EditCardForm(
    uiState: EditCardUiState,
    onFrontChange: (String) -> Unit,
    onBackChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // FRONT (Pertanyaan)
        Text(
            text = "Pertanyaan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        OutlinedTextField(
            value = uiState.front,
            onValueChange = onFrontChange,
            label = { Text("Tulis pertanyaan di sini") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 5,
            enabled = !uiState.isSaving,
        )

        // BACK (Jawaban)
        Text(
            text = "Jawaban",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp),
        )
        OutlinedTextField(
            value = uiState.back,
            onValueChange = onBackChange,
            label = { Text("Tulis jawaban di sini") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 8,
            enabled = !uiState.isSaving,
        )

        // ERROR MESSAGE
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        // SAVE BUTTON
        Button(
            onClick = onSave,
            enabled = uiState.canSave,
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text("Menyimpan...")
            } else {
                Icon(Icons.Default.Check, contentDescription = null)
                Text(
                    text = "Simpan Perubahan",
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}