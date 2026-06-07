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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neurodeck.presentation.components.ErrorMessage
import com.example.neurodeck.presentation.components.LoadingIndicator
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when {
                uiState.isLoading -> LoadingIndicator()

                uiState.errorMessage != null && uiState.front.isEmpty() -> {
                    // Error saat load — tampil full-screen
                    ErrorMessage(message = uiState.errorMessage!!)
                }

                else -> EditCardForm(
                    uiState = uiState,
                    onFrontChange = viewModel::onFrontChange,
                    onBackChange = viewModel::onBackChange,
                    onSave = { viewModel.saveCard(onSuccess = onSaved) },
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
        //FRONT (Pertanyaan)
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

        // BACK (Jawaban
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
