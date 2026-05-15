package com.dailybliss.app.presentation.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dailybliss.app.presentation.components.LoadingIndicator
import com.dailybliss.app.presentation.components.TypingIndicator
import com.dailybliss.app.presentation.util.rememberImagePickerLauncher
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    onNavigateBack: () -> Unit,
    viewModel: AIAssistantViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    
    val imagePicker = rememberImagePickerLauncher(
        onResult = { bytes -> viewModel.onImageSelected(bytes) }
    )
    
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Simple Header aligned at the very top
        CenterAlignedTopAppBar(
            title = { 
                Text(
                    "Asisten AI", 
                    color = MaterialTheme.colorScheme.primary, 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.primary)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        // Chat messages area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.messages.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Halo! Bagikan hal baikmu hari ini dan saya akan meresponnya.",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                items(uiState.messages) { message ->
                    ChatBubble(message)
                }
            }
            
            uiState.error?.let {
                item {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        
        // Chat Input
        ChatInput(
            message = uiState.input,
            selectedImage = uiState.selectedImageBytes,
            onMessageChange = viewModel::onInputChange,
            onSend = viewModel::sendMessage,
            onPickImage = { imagePicker.launch() },
            onRemoveImage = { viewModel.onImageSelected(null) },
            enabled = !uiState.isLoading
        )

        // DYNAMIC SPACER: This is the critical fix.
        // It calculates the exact space needed for EITHER the Navigation Bar (when keyboard is closed)
        // OR the Keyboard (when open), but never both simultaneously.
        val imeBottom = WindowInsets.ime.getBottom(density)
        val navBarBottom = WindowInsets.navigationBars.getBottom(density)
        // 80.dp is the fixed height of the NavigationBar composable used in AppNavHost
        val bottomBarHeightPx = with(density) { 80.dp.roundToPx() }
        val totalNavBarHeight = navBarBottom + bottomBarHeightPx

        val spacerHeightPx = maxOf(imeBottom, totalNavBarHeight)
        val spacerHeightDp = with(density) { spacerHeightPx.toDp() }

        Spacer(Modifier.height(spacerHeightDp))
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUser) 
                MaterialTheme.colorScheme.primary
            else 
                MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 20.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 20.dp
            ),
            modifier = Modifier.widthIn(max = 300.dp),
            shadowElevation = if (isUser) 2.dp else 0.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                if (message.imageBytes != null) {
                    AsyncImage(
                        model = message.imageBytes,
                        contentDescription = "User Attachment",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(bottom = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                
                if (message.text.isNotBlank()) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp
                        ),
                        color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (message.role == "model") {
                    TypingIndicator(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun ChatInput(
    message: String,
    selectedImage: ByteArray?,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (selectedImage != null) {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = onRemoveImage, 
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Close, 
                            null, 
                            tint = Color.White, 
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPickImage, enabled = enabled, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.AddAPhoto, "Add Media", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                BasicTextField(
                    value = message,
                    onValueChange = onMessageChange,
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (message.isEmpty()) {
                            Text("Bagikan syukurmu...", color = MaterialTheme.colorScheme.outline, style = MaterialTheme.typography.bodyLarge)
                        }
                        innerTextField()
                    },
                    enabled = enabled
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = onSend,
                    enabled = enabled && (message.isNotBlank() || selectedImage != null),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = if (enabled && (message.isNotBlank() || selectedImage != null)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
