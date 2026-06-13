package com.example.foodsaver.presentation.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    initialText: String?,
    onNavigateBack: () -> Unit,
    viewModel: AIAssistantViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val mainActions = remember {
        listOf(
            AIAction.CHECK_STOCK,
            AIAction.CREATE_RECIPE,
            AIAction.STORAGE_TIPS,
            AIAction.SUMMARIZE_INVENTORY,
            AIAction.COOKING_IDEAS
        )
    }

    LaunchedEffect(initialText) {
        viewModel.setInitialText(initialText)
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AIAssistantEvent.CopyToClipboard -> {
                    snackbarHostState.showSnackbar("Disalin ke papan klip")
                }
                else -> {} 
            }
        }
    }
    
    Scaffold(
        modifier = Modifier.testTag("ai_assistant_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AIAssistantTopBar(onBackClick = onNavigateBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            ActionSelectorSection(
                actions = mainActions,
                selectedAction = uiState.selectedAction,
                onActionSelected = viewModel::onActionSelected
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            InputSection(
                inputText = uiState.inputText,
                onInputTextChange = viewModel::onInputTextChange,
                selectedAction = uiState.selectedAction,
                error = uiState.error
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            QuickPromptsSection(
                selectedAction = uiState.selectedAction,
                onPromptClick = { prompt -> viewModel.onQuickPromptClicked(prompt, uiState.selectedAction) }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            ExecuteButton(
                isLoading = uiState.isLoading,
                canExecute = uiState.canExecute,
                onClick = { viewModel.executeAction() }
            )
            
            ResultSection(
                result = uiState.result,
                selectedAction = uiState.selectedAction,
                onCopyClick = viewModel::copyResult
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AIAssistantTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Asisten AI FoodSaver", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionSelectorSection(
    actions: List<AIAction>,
    selectedAction: AIAction,
    onActionSelected: (AIAction) -> Unit
) {
    Column {
        Text(
            text = "Bagaimana AI bisa membantumu?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
            modifier = Modifier.testTag("ai_action_row")
        ) {
            items(actions) { action ->
                FilterChip(
                    selected = selectedAction == action,
                    onClick = { onActionSelected(action) },
                    label = { Text(action.displayName) },
                    leadingIcon = if (selectedAction == action) {
                        { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null,
                    modifier = Modifier.testTag("chip_ai_${action.name}")
                )
            }
        }
        
        Text(
            text = selectedAction.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InputSection(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    selectedAction: AIAction,
    error: String?
) {
    OutlinedTextField(
        value = inputText,
        onValueChange = onInputTextChange,
        label = { Text("Tanya AI FoodSaver") },
        placeholder = { Text(selectedAction.placeholder) },
        minLines = 3,
        maxLines = 8,
        isError = error != null,
        supportingText = error?.let { 
            { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning, 
                        contentDescription = null, 
                        modifier = Modifier.size(14.dp), 
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(it) 
                }
            } 
        },
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth().testTag("ai_input_field")
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickPromptsSection(
    selectedAction: AIAction,
    onPromptClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Saran Cepat:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val quickPrompts = when (selectedAction) {
                AIAction.CHECK_STOCK -> listOf("Cek bahan expired hari ini", "Bahan paling urgent")
                AIAction.CREATE_RECIPE -> listOf("Buat resep praktis 15 menit", "Menu makan malam sehat")
                AIAction.STORAGE_TIPS -> listOf("Tips simpan sayur hijau", "Cara awetkan daging")
                AIAction.SUMMARIZE_INVENTORY -> listOf("Ringkas stok hari ini", "Berapa banyak stok aman?")
                AIAction.COOKING_IDEAS -> listOf("Ide masak telur", "Ide camilan sehat")
                else -> listOf("Tips kurangi food waste")
            }
            
            quickPrompts.forEach { prompt ->
                SuggestionChip(
                    onClick = { onPromptClick(prompt) },
                    label = { Text(prompt, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Lightbulb, null, Modifier.size(14.dp)) },
                    modifier = Modifier.testTag("suggestion_$prompt")
                )
            }
        }
    }
}

@Composable
private fun ExecuteButton(
    isLoading: Boolean,
    canExecute: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = canExecute,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth().testTag("btn_ask_ai")
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp).padding(end = 8.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
            Text("Memproses Data...")
        } else {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(18.dp).padding(end = 8.dp)
            )
            Text("Tanya AI")
        }
    }
}

@Composable
private fun ResultSection(
    result: String?,
    selectedAction: AIAction,
    onCopyClick: () -> Unit
) {
    AnimatedVisibility(visible = result != null) {
        Column {
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analisis AI FoodSaver:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth().testTag("ai_result_card"),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = result ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onCopyClick,
                            modifier = Modifier.weight(1f).testTag("btn_copy_ai_result"),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Salin")
                        }
                        
                        if (selectedAction == AIAction.CREATE_RECIPE) {
                            Button(
                                onClick = { /* Future: Implement save to favorite */ },
                                modifier = Modifier.weight(1.2f),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("Jadikan Resep")
                            }
                        }
                    }
                }
            }
        }
    }
}
