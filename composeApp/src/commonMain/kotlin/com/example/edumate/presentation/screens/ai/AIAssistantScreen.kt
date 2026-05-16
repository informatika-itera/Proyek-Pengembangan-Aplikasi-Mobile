package com.example.edumate.presentation.screens.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    noteId: Long? = null,
    initialText: String? = null,
    onNavigateBack: () -> Unit,
    onApplyResult: ((String) -> Unit)? = null,
    viewModel: AIAssistantViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var topic by remember { mutableStateOf(initialText ?: "") }

    LaunchedEffect(initialText) {
        if (!initialText.isNullOrBlank() && topic.isBlank()) {
            topic = initialText
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant EduMate") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = uiState.mode == AIWorkMode.IDEAS,
                onClick = { viewModel.setMode(AIWorkMode.IDEAS) },
                label = { Text("Ide Tugas") },
                leadingIcon = { Icon(Icons.Default.Checklist, contentDescription = null) }
            )
            FilterChip(
                selected = uiState.mode == AIWorkMode.SUMMARY,
                onClick = { viewModel.setMode(AIWorkMode.SUMMARY) },
                label = { Text("Ringkas Catatan") },
                leadingIcon = { Icon(Icons.Default.Summarize, contentDescription = null) }
            )
            FilterChip(
                selected = uiState.mode == AIWorkMode.IMPROVE,
                onClick = { viewModel.setMode(AIWorkMode.IMPROVE) },
                label = { Text("Perbaiki Tulisan") },
                leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) }
            )

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Topik / teks") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Button(
                onClick = {
                    when (uiState.mode) {
                        AIWorkMode.IDEAS -> viewModel.generateIdeas(topic)
                        AIWorkMode.SUMMARY -> viewModel.summarizeNote(topic)
                        AIWorkMode.IMPROVE -> viewModel.improveWriting(topic)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && topic.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp).width(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI sedang memproses...")
                } else {
                    Text(
                        when (uiState.mode) {
                            AIWorkMode.IDEAS -> "Gunakan AI (Ide)"
                            AIWorkMode.SUMMARY -> "Gunakan AI (Ringkas)"
                            AIWorkMode.IMPROVE -> "Gunakan AI (Perbaiki)"
                        }
                    )
                }
            }

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            uiState.result?.let { result ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hasil AI", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(result)
                        if (onApplyResult != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(onClick = { onApplyResult(result) }) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gunakan hasil ini")
                            }
                        }
                    }
                }
            }

            if (uiState.ideas.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Langkah yang disarankan", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        uiState.ideas.forEachIndexed { index, idea ->
                            Text("${index + 1}. $idea")
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
