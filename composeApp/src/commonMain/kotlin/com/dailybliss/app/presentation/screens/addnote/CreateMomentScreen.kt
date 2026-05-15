package com.dailybliss.app.presentation.screens.addnote

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun CreateMomentScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateMomentViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var activeBlockIndex by remember { mutableStateOf<Int?>(null) }
    
    // Focus management
    val focusRequesters = remember(uiState.contentBlocks.size) {
        List(uiState.contentBlocks.size) { FocusRequester() }
    }
    
    LaunchedEffect(uiState.requestedFocusIndex) {
        uiState.requestedFocusIndex?.let { index ->
            if (index in focusRequesters.indices) {
                try {
                    focusRequesters[index].requestFocus()
                    viewModel.clearFocusRequest()
                } catch (e: Exception) {}
            }
        }
    }
    
    val singleImagePicker = rememberImagePickerLauncher(
        onResult = { uri ->
            uri?.let { 
                activeBlockIndex?.let { index ->
                    viewModel.addImageBlock(it, afterIndex = index)
                }
            }
        }
    )
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateMomentEvent.MomentSaved -> onNavigateBack()
                is CreateMomentEvent.Error -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }
    
    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Gray)
                        }
                        
                        BasicTextField(
                            value = uiState.title,
                            onValueChange = viewModel::onTitleChange,
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 34.sp
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            decorationBox = { innerTextField ->
                                if (uiState.title.isEmpty()) {
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
                                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                        )
                        
                        Button(
                            onClick = { viewModel.saveMoment() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp).padding(top = 4.dp)
                        ) {
                            Text("Simpan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                itemsIndexed(uiState.contentBlocks) { index, block ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        when (block) {
                            is ContentBlock.Text -> {
                                TextBlockItem(
                                    text = block.text,
                                    onTextChange = { viewModel.onBlockChange(index, block.copy(text = it)) },
                                    onRemove = { viewModel.removeBlock(index) },
                                    onEnterPressed = { viewModel.addTextBlock(afterIndex = index) },
                                    onAttachMedia = { 
                                        activeBlockIndex = index
                                        singleImagePicker.launch()
                                    },
                                    focusRequester = focusRequesters[index]
                                )
                            }
                            is ContentBlock.Image -> {
                                val isLast = index == uiState.contentBlocks.lastIndex
                                val noTextBelow = uiState.contentBlocks.getOrNull(index + 1) !is ContentBlock.Text
                                ImageBlockItem(
                                    url = block.url,
                                    showAddBlockButton = isLast || noTextBelow,
                                    onRemove = { viewModel.removeBlock(index) },
                                    onAddBlockBelow = { viewModel.addTextBlock(afterIndex = index) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
