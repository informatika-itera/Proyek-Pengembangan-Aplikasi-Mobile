package com.dailybliss.app.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dailybliss.app.domain.model.ContentBlock
import com.dailybliss.app.presentation.components.*
import com.dailybliss.app.presentation.util.rememberImagePickerLauncher
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentDetailScreen(
    momentId: Long,
    onNavigateBack: () -> Unit,
    viewModel: MomentDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var activeBlockIndex by remember { mutableStateOf<Int?>(null) }
    
    // Unified Focus Management
    val blocksSize = (uiState as? MomentDetailUiState.Success)?.contentBlocks?.size ?: 0
    val focusRequesters = remember(blocksSize) {
        List(blocksSize) { FocusRequester() }
    }
    
    LaunchedEffect(uiState) {
        val state = uiState
        if (state is MomentDetailUiState.Success) {
            state.requestedFocusIndex?.let { index ->
                if (index in focusRequesters.indices) {
                    try {
                        focusRequesters[index].requestFocus()
                        viewModel.clearFocusRequest()
                    } catch (e: Exception) {}
                }
            }
        }
    }
    
    val singleImagePicker = rememberImagePickerLauncher(
        onResult = { uri ->
            uri?.let { 
                activeBlockIndex?.let { index ->
                    viewModel.addImageBlock(it, index)
                }
            }
        }
    )
    
    LaunchedEffect(momentId) {
        viewModel.loadMoment(momentId)
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MomentDetailEvent.MomentDeleted -> onNavigateBack()
                else -> {}
            }
        }
    }
    
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteMoment()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
    
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        when (val state = uiState) {
            is MomentDetailUiState.Loading -> LoadingIndicator()
            is MomentDetailUiState.Success -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onNavigateBack
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Gray)
                            }
                            
                            BasicTextField(
                                value = state.title,
                                onValueChange = viewModel::onTitleChange,
                                textStyle = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 34.sp
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                decorationBox = { innerTextField ->
                                    if (state.title.isEmpty()) {
                                        Text(
                                            text = "Judul...",
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = Color.LightGray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    innerTextField()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            )
                            
                            IconButton(
                                onClick = { showDeleteDialog = true }
                            ) {
                                Icon(Icons.Outlined.Delete, "Delete", tint = Color.Gray)
                            }
                        }
                    }

                    itemsIndexed(state.contentBlocks) { index, block ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            when (block) {
                                is ContentBlock.Text -> {
                                    TextBlockItem(
                                        text = block.text,
                                        onTextChange = { viewModel.onBlockChange(index, block.copy(text = it)) },
                                        onRemove = { viewModel.removeBlock(index) },
                                        onEnterPressed = { viewModel.addTextBlock(index) },
                                        onAttachMedia = { 
                                            activeBlockIndex = index
                                            singleImagePicker.launch()
                                        },
                                        focusRequester = if (index < focusRequesters.size) focusRequesters[index] else FocusRequester()
                                    )
                                }
                                is ContentBlock.Image -> {
                                    val isLast = index == state.contentBlocks.lastIndex
                                    val noTextBelow = state.contentBlocks.getOrNull(index + 1) !is ContentBlock.Text
                                    ImageBlockItem(
                                        url = block.url,
                                        showAddBlockButton = isLast || noTextBelow,
                                        onRemove = { viewModel.removeBlock(index) },
                                        onAddBlockBelow = { viewModel.addTextBlock(index) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is MomentDetailUiState.NotFound -> {
                EmptyState("Tidak Ditemukan", "Momen mungkin telah dihapus.")
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Momen?") },
        text = { Text("Tindakan ini tidak dapat dibatalkan.") },
        confirmButton = {
            TextButton(onClick = onConfirm, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text("HAPUS")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)) {
                Text("BATAL")
            }
        },
        containerColor = Color.White
    )
}
