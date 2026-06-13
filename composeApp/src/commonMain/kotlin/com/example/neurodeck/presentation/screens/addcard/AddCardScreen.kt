package com.example.neurodeck.presentation.screens.addcard

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    deckId: Long,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddCardViewModel = koinViewModel { parametersOf(deckId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kartu Baru") },
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // FRONT CARD (Pertanyaan)
            Text(
                text = "Pertanyaan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = uiState.front,
                onValueChange = viewModel::onFrontChange,
                label = { Text("Tulis pertanyaan di sini") },
                placeholder = { Text("Contoh: Apa itu Kotlin Multiplatform?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 5,
                enabled = !uiState.isSaving,
            )

            // BACK CARD (Jawaban)
            Text(
                text = "Jawaban",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp),
            )
            OutlinedTextField(
                value = uiState.back,
                onValueChange = viewModel::onBackChange,
                label = { Text("Tulis jawaban di sini") },
                placeholder = { Text("Contoh: Framework untuk share kode antar platform mobile.") },
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
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        viewModel.saveCard(onSuccess = {
                            // Snackbar jalan paralel (tidak blok navigasi)
                            scope.launch {
                                snackbarHostState.showSnackbar("✅ Kartu berhasil ditambahkan!")
                            }
                            // Navigasi balik setelah jeda singkat biar popup sempat kelihatan
                            scope.launch {
                                delay(600)
                                onSaved()
                            }
                        })
                    },
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
                            text = "Simpan Kartu",
                            modifier = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}