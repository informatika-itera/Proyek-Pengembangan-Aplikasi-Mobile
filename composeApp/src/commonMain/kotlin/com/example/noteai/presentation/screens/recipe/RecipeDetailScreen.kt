package com.example.noteai.presentation.screens.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: RecipeDetailViewModel = koinViewModel()
) {
    val recipe by viewModel.recipe.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Resep") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    recipe?.let { r ->
                        IconButton(onClick = { viewModel.toggleFavorite(recipeId) }) {
                            Icon(
                                imageVector = if (r.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (r.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { onNavigateToEdit(recipeId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = {
                            viewModel.removeRecipe(recipeId) {
                                onNavigateBack()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (recipe == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Resep tidak ditemukan")
            }
        } else {
            val r = recipe!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(r.title, style = MaterialTheme.typography.headlineMedium)
                
                if (r.isAiGenerated) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("AI Generated") }
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Bahan-bahan", 
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(r.ingredients, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Instruksi Memasak", 
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(r.instructions, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
