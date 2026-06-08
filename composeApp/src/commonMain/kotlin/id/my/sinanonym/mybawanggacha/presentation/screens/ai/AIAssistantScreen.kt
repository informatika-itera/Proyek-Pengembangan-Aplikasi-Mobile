package id.my.sinanonym.mybawanggacha.presentation.screens.ai

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.sinanonym.mybawanggacha.domain.ai.repository.ChatMessage
import id.my.sinanonym.mybawanggacha.domain.ai.repository.MessageSender
import id.my.sinanonym.mybawanggacha.presentation.components.MBGTopBar
import id.my.sinanonym.mybawanggacha.presentation.screens.ai.components.AiModelMenu
import id.my.sinanonym.mybawanggacha.presentation.screens.ai.components.ChatBubble
import id.my.sinanonym.mybawanggacha.presentation.screens.ai.components.LoadingBubble
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    noteId: Long?,
    initialText: String?,
    animeContext: String?,
    mediaId: Int? = null,
    mediaType: String? = null,
    mediaTitle: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit = {},
    onNavigateToMangaDetail: (Int) -> Unit = {},
    onApplyResult: ((String) -> Unit)? = null,
    viewModel: AIAssistantViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    
    LaunchedEffect(initialText) {
        viewModel.setInitialText(initialText)
    }
    
    LaunchedEffect(noteId, mediaId, mediaType, mediaTitle, animeContext) {
        viewModel.configureSession(
            noteId = noteId,
            mediaId = mediaId,
            mediaType = mediaType,
            mediaTitle = mediaTitle,
            context = animeContext
        )
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AIAssistantEvent.CopyToClipboard -> {
                    clipboardManager.setText(AnnotatedString(event.text))
                    snackbarHostState.showSnackbar("Disalin ke clipboard")
                }
                is AIAssistantEvent.ApplyToNote -> {
                    onApplyResult?.invoke(event.text)
                    snackbarHostState.showSnackbar("Diterapkan ke catatan")
                    onNavigateBack()
                }
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MBGTopBar(
                title = uiState.aiApiModel.label,
                onNavigateBack = onNavigateBack,
                titleContent = {
                    AiModelMenu(
                        selected = uiState.aiApiModel,
                        onSelected = viewModel::setAiApiModel
                    )
                },
                actions = {
                    IconButton(onClick = viewModel::resetSession) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset session"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.chatHistory) { message ->
                    ChatBubble(
                        message = message,
                        noteId = noteId,
                        onCopy = { viewModel.copyResult(message.text) },
                        onApply = { viewModel.applyToNote(message.text) },
                        onMediaClick = { reference ->
                            val malId = reference.malId ?: return@ChatBubble
                            when (reference.normalizedType) {
                                "anime" -> onNavigateToAnimeDetail(malId)
                                else -> onNavigateToMangaDetail(malId)
                            }
                        }
                    )
                }
                if (uiState.isLoadingSession) {
                    item {
                        Text(
                            text = "Memuat session chat...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (uiState.isLoading) {
                    item {
                        LoadingBubble()
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = viewModel::onInputTextChange,
                    placeholder = { Text("Ketik pesan...") },
                    isError = uiState.error != null,
                    supportingText = uiState.error?.let { { Text(it) } },
                    modifier = Modifier.weight(1f),
                    maxLines = 4
                )
                IconButton(
                    onClick = { viewModel.executeAction() },
                    enabled = uiState.canExecute,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim"
                    )
                }
            }
        }
    }
}
