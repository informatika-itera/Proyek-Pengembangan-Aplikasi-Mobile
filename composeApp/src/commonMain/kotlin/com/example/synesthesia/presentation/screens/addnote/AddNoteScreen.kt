package com.example.synesthesia.presentation.screens.addnote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import com.example.synesthesia.presentation.components.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.presentation.components.ColorPickerRow
import com.example.synesthesia.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    noteId: Long?,
    onNavigateBack: () -> Unit,
    onNavigateToAI: (String) -> Unit,
    viewModel: AddNoteViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(noteId) {
        noteId?.let { viewModel.loadNote(it) }
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddNoteEvent.NoteSaved -> onNavigateBack()
                is AddNoteEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }
    
    AuroraBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    title = { 
                        Text(
                            if (uiState.isEditMode) "Edit Catatan" else "Catatan Baru",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { onNavigateToAI(uiState.content) },
                            enabled = uiState.content.isNotBlank()
                        ) {
                            Icon(Icons.Outlined.AutoAwesome, contentDescription = "AI Assistant")
                        }
                        
                        IconButton(
                            onClick = { viewModel.saveNote() },
                            enabled = uiState.canSave
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Simpan")
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                LoadingIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            TextField(
                                value = uiState.title,
                                onValueChange = viewModel::onTitleChange,
                                placeholder = { Text("Judul", color = Color.White.copy(alpha = 0.5f)) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                textStyle = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            TextField(
                                value = uiState.content,
                                onValueChange = viewModel::onContentChange,
                                placeholder = { Text("Tulis catatan di sini...", color = Color.White.copy(alpha = 0.4f)) },
                                minLines = 10,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White.copy(alpha = 0.8f)
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    CategoryDropdown(
                        selectedCategory = uiState.category,
                        onCategorySelected = viewModel::onCategoryChange
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Aksen Warna",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ColorPickerRow(
                        selectedColor = uiState.color,
                        onColorSelected = viewModel::onColorChange
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selectedCategory: NoteCategory,
    onCategorySelected: (NoteCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedCategory.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Kategori", color = Color.White.copy(alpha = 0.8f)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedBorderColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1C1B1F))
        ) {
            NoteCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.displayName, color = Color.White) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}
