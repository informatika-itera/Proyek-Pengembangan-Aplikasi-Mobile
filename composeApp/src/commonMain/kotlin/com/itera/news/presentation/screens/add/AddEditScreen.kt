package com.itera.news.presentation.screens.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    url: String?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val source by viewModel.source.collectAsState()
    val category by viewModel.category.collectAsState()

    LaunchedEffect(url) {
        viewModel.initArticle(url)
    }

    LaunchedEffect(uiState) {
        if (uiState is AddEditUiState.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (url.isNullOrEmpty()) "Add Article" else "Edit Article") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is AddEditUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AddEditUiState.Error -> {
                    val message = (uiState as AddEditUiState.Error).message
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.initArticle(url) }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = viewModel::onTitleChange,
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = uiState is AddEditUiState.Error && title.isBlank()
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = viewModel::onDescriptionChange,
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            isError = uiState is AddEditUiState.Error && description.isBlank()
                        )
                        OutlinedTextField(
                            value = source,
                            onValueChange = viewModel::onSourceChange,
                            label = { Text("Source") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = category,
                            onValueChange = viewModel::onCategoryChange,
                            label = { Text("Category") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = viewModel::saveArticle,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
